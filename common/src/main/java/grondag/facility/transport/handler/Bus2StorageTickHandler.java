package grondag.facility.transport.handler;

import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.transact.Transaction;

public class Bus2StorageTickHandler implements TransportTickHandler {
	public static final Bus2StorageTickHandler INSTANCE = new Bus2StorageTickHandler();

	@Override
	public boolean tick(TransportContext context)  {
		final TransportStorageContext storageContext = context.storageContext();

		if (!storageContext.prepareForTick())  {
			return false;
		}

		final TransportCarrierContext carrierContext = context.carrierContext();

		if(!carrierContext.isReady()) {
			return true;
		}

		// see if we know what we want
		Article targetArticle = storageContext.proposeAccept(carrierContext.articleType);

		// if not accepting anything then exit
		if (targetArticle == null) {
			return true;
		}

		final boolean didStoragePropose;

		if (targetArticle.isNothing()) {
			didStoragePropose = false;
			// local storage has no preference, so find something random on network
			targetArticle = carrierContext.anyAvailableArticle();

			// if still nothing, then try again next time
			if (targetArticle.isNothing()) {
				return true;
			}

			// if can't accept, tell carrier to find something different next time and try again next tick
			if (!storageContext.canAccept(targetArticle)) {
				carrierContext.resetAvailableArticle();
				return true;
			}
		} else {
			didStoragePropose = true;
		}

		final ArticleFunction supplier = carrierContext.sourceFor(targetArticle);

		if (supplier == null) {
			if (didStoragePropose) {
				storageContext.advanceAcceptProposal(carrierContext.articleType);
			}
			return true;
		}

		final ArticleFunction bufferConsumer = context.buffer().consumer();
		final long units  = storageContext.unitsFor(targetArticle);
		long howMany = storageContext.capacityFor(targetArticle, units);
		howMany = bufferConsumer.apply(targetArticle, howMany, units, true);
		howMany = carrierContext.throttle(targetArticle, howMany, units, true);
		howMany = supplier.apply(targetArticle, howMany, units, true);

		if (howMany > 0) {
			try (Transaction tx = Transaction.open()) {
				tx.enlist(supplier);
				tx.enlist(bufferConsumer);

				howMany = carrierContext.throttle(targetArticle, howMany, units, false);
				howMany = supplier.apply(targetArticle, howMany, units, false);

				if (howMany > 0  && bufferConsumer.apply(targetArticle, howMany, units, false) == howMany) {
					tx.commit();
					carrierContext.resetCooldown();
				}
			}
		}

		return true;
	}
}
