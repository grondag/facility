package grondag.facility.transport.handler;

import grondag.facility.transport.buffer.TransportBuffer;
import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.transact.Transaction;
import grondag.fluidity.wip.api.transport.CarrierNode;

public class Bus2StorageTickHandler implements TransportTickHandler {
	public static final Bus2StorageTickHandler INSTANCE = new Bus2StorageTickHandler();

	@Override
	public boolean tick(TransportContext context)  {
		if (!context.isValid())  {
			return false;
		}

		final TransportStorageContext storageContext = context.storageContext();

		if (!storageContext.isValid()) {
			return false;
		}

		if(storageContext.isFull()) {
			return true;
		}

		final TransportBuffer buffer = context.buffer();

		if  (!buffer.clearBuffer(storageContext)) {
			return true;
		}

		final TransportCarrierContext carrierContext = context.carrierContext();

		if(!carrierContext.isReady()) {
			return true;
		}

		Article targetArticle = carrierContext.targetArticle;
		CarrierNode targetNode = carrierContext.lastTarget();

		// try to repeat what we did last time if possible
		if (!targetArticle.isNothing()) {
			if (!storageContext.canAccept(targetArticle)) {
				// need to pick a different article
				targetArticle = Article.NOTHING;
			} else {
				// try existing source first
				if (!targetNode.isValid() || !targetNode.getComponent(ArticleFunction.SUPPLIER_COMPONENT).get().canApply(targetArticle)) {
					// existing source is bust, look for a new source
					targetNode = carrierContext.sourceFor(targetArticle);
				}

				if (!targetNode.isValid()) {
					// what we want isn't available
					targetArticle = Article.NOTHING;
				}
			}
		}

		// see if we know what we want
		if (targetArticle.isNothing()) {
			if (storageContext.hasContentPreference()) {
				if (!storageContext.hasNext()) {
					storageContext.beginIterating();
				}

				int attempt = 0;

				while(storageContext.hasNext() && ++attempt <= 16) {
					final StoredArticleView view =  storageContext.next();
					final Article a = view.article();

					if (!a.isNothing()) {
						targetNode = carrierContext.sourceFor(a);

						if (targetNode.isValid()) {
							targetArticle = a;
							break;
						}
					}
				}
			}

			// local storage did not match anything available, so find something random on network
			if (targetArticle.isNothing()) {

				// try existing source first
				if (targetNode.isValid()) {
					targetArticle = carrierContext.randomArticleFromSource(targetNode);
				}

				// if that didn't work, then need a different node
				if (targetArticle.isNothing()) {
					targetNode = carrierContext.randomSource();
					targetArticle = carrierContext.randomArticleFromSource(targetNode);

					if (targetArticle.isNothing()) {
						// if still nothing, then try again next time
						return true;
					}
				}
			}
		}

		carrierContext.targetArticle = targetArticle;

		if (!targetNode.isValid()) {
			targetNode = carrierContext.sourceFor(targetArticle);

			if (!targetNode.isValid()) {
				return true;
			}
		}

		final ArticleFunction supplier = targetNode.getComponent(ArticleFunction.SUPPLIER_COMPONENT).get();
		final long units  = storageContext.unitsFor(targetArticle);
		final long roomFor = storageContext.capacityFor(targetArticle, units);
		final long available = supplier.apply(targetArticle, roomFor, units, true);

		if (available > 0) {
			assert Transaction.current() == null;

			try (Transaction tx = Transaction.open()) {
				tx.enlist(supplier);
				tx.enlist(buffer);

				long howMany = carrierContext.throttle(targetArticle, available, units, false);
				howMany = supplier.apply(targetArticle, howMany, units, false);

				if (howMany > 0  && buffer.accept(targetArticle, howMany, units, false) ==  howMany) {
					tx.commit();
					carrierContext.resetCooldown();
				}
			}

			buffer.clearBuffer(storageContext);
		}

		return true;
	}
}
