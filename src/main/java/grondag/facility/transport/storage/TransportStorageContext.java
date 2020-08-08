package grondag.facility.transport.storage;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;

public interface TransportStorageContext {

	boolean isValid();

	boolean isFull();

	boolean canAccept(Article targetArticle);

	boolean isEmpty();

	boolean hasContentPreference();

	void beginIterating();

	boolean hasNext();

	StoredArticleView next();

	long unitsFor(Article targetArticle);

	long capacityFor(Article targetArticle, long divisor);

	long accept(Article storedArticle, long numerator, long divisor);

	boolean canSupply(Article targetArticle);

	long available(Article targetArticle, long units);

	long supply(Article targetArticle, long howMany, long units);

}
