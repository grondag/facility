package grondag.facility.transport.handler;

import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.storage.ArticleFunction;

public class Storage2BusTickHandler implements TransportTickHandler {
	public static final Storage2BusTickHandler INSTANCE = new Storage2BusTickHandler();

	@Override
	public boolean tick(TransportContext context)  {
		final TransportStorageContext storageContext = context.storageContext();

		if (!storageContext.prepareForTick()) {
			return false;
		}

		final TransportCarrierContext carrierContext = context.carrierContext();

		// see if we have something to send
		final Article targetArticle = storageContext.proposeSupply(carrierContext.articleType);

		// storage did not have anything available, try again next time
		if (targetArticle.isNothing()) {
			return true;
		}

		final long units = storageContext.unitsFor(targetArticle);
		long howMany = storageContext.available(targetArticle, units);

		if (howMany > 0) {
			// find out how many buffer can hold
			final ArticleFunction bufferConsumer = context.buffer().consumer();
			howMany = bufferConsumer.apply(targetArticle, howMany, units, true);

			// move to buffer
			howMany = storageContext.supply(targetArticle, howMany, units);
			final long check  = bufferConsumer.apply(targetArticle, howMany, units, false);

			assert check == howMany;
		}

		return true;
	}
}
