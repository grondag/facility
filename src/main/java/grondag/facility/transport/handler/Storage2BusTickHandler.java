package grondag.facility.transport.handler;

import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.ArticleFunction;

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

		final TransportCarrierContext carrierContext = context.carrierContext();

		Article targetArticle = carrierContext.targetArticle;

		// try to repeat what we did last time if possible
		if (!targetArticle.isNothing() && !storageContext.canSupply(targetArticle)) {
			// need to pick a different article
			targetArticle = Article.NOTHING;
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

		// storage did not have anything available, try again next time
		if (targetArticle.isNothing()) {
			return true;
		}

		final long units = storageContext.unitsFor(targetArticle);
		long howMany = storageContext.available(targetArticle, units);

		if (howMany > 0) {
			final ArticleFunction bufferConsumer = context.buffer().consumer();
			// find out how many buffer can hold
			howMany = bufferConsumer.apply(targetArticle, howMany, units, true);

			// move to buffer
			howMany = storageContext.supply(targetArticle, howMany, units);
			final long check  = bufferConsumer.apply(targetArticle, howMany, units, false);

			assert check == howMany;
		}

		return true;
	}
}
