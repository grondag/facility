package grondag.facility.transport.handler;

import grondag.facility.transport.buffer.TransportBuffer;
import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.wip.api.transport.CarrierNode;

public class Storage2BusTickHandler implements TransportTickHandler {
	public static final Storage2BusTickHandler INSTANCE = new Storage2BusTickHandler();

	@Override
	public boolean tick(TransportContext context)  {
		if (!context.isValid())  {
			return false;
		}

		final TransportStorageContext storageContext = context.storageContext();

		if (!storageContext.isValid()) {
			return false;
		}

		if(storageContext.isEmpty()) {
			return true;
		}

		final TransportBuffer buffer = context.buffer();
		final TransportCarrierContext carrierContext = context.carrierContext();

		if  (!buffer.clearBuffer(carrierContext)) {
			return true;
		}

		Article targetArticle = carrierContext.targetArticle;
		CarrierNode targetNode = carrierContext.lastTarget();

		// try to repeat what we did last time if possible
		if (!targetArticle.isNothing()) {
			if (!storageContext.canSupply(targetArticle)) {
				// need to pick a different article
				targetArticle = Article.NOTHING;
			} else {
				// try existing consumer first
				if (!targetNode.isValid() || !targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get().canApply(targetArticle)) {
					// existing source is bust, look for a new source
					targetNode = carrierContext.consumerFor(targetArticle);
				}

				if (!targetNode.isValid()) {
					// what we want isn't available
					targetArticle = Article.NOTHING;
				}
			}
		}

		// see if we know what we have
		if (targetArticle.isNothing()) {
			if (!storageContext.hasNext()) {
				storageContext.beginIterating();
			}

			int attempt = 0;

			// the limit here is for local iteration - will only try to find consumer on network 1X
			while(storageContext.hasNext() && ++attempt <= 16) {
				final StoredArticleView view =  storageContext.next();
				final Article a = view.article();

				if (!a.isNothing()) {
					targetArticle = a;
					carrierContext.targetArticle = targetArticle;
					break;
				}
			}
		}

		// local storage did not hace anything available, try agin next time
		if (targetArticle.isNothing()) {
			return true;
		}

		if (!targetNode.isValid() || !targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get().canApply(targetArticle)) {
			targetNode = carrierContext.consumerFor(targetArticle);

			if (!targetNode.isValid()) {
				return true;
			}
		}

		final ArticleFunction consumer = targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get();
		final long units  = storageContext.unitsFor(targetArticle);
		long howMany = storageContext.available(targetArticle, units);
		howMany = consumer.apply(targetArticle, howMany, units, true);
		howMany = carrierContext.throttle(targetArticle, howMany, units, true);

		if (howMany > 0) {
			howMany = storageContext.supply(targetArticle, howMany, units);
			final long check = buffer.accept(targetArticle, howMany, units, false);
			assert check == howMany;
			buffer.clearBuffer(carrierContext);
		}

		return true;
	}

}
