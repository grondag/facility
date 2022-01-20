package grondag.facility.transport.storage;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import org.jetbrains.annotations.Nullable;

public interface TransportStorageContext {
	boolean prepareForTick();

	boolean canAccept(Article targetArticle);

	/**
	 * Find an item the stora can supply.  Subsequent calls iterate choices. Eventually wraps.
	 * @param type Fluid or Item (or others in future)
	 * @return Article.NOTHING if not supplying anything at this time.
	 */
	Article proposeSupply(ArticleType<?> type);

	/**
	 * Find an item the stora can accept. Subsequent calls iterate choices. Eventually wraps.
	 * @param type Fluid or Item (or others in future)
	 * @return Article.NOTHING if no preference, null if not accepting anything at this time.
	 */
	@Nullable Article proposeAccept(ArticleType<?> type);

	/**
	 * Called by handler when {@link #proposeAccept(ArticleType)} return a non-null, non-nothing
	 * article and that article is not available. Signals storage context to propose something
	 * different if possible.
	 * @param articleType
	 */
	void advanceAcceptProposal(ArticleType<?> articleType);

	long unitsFor(Article targetArticle);

	long capacityFor(Article targetArticle, long divisor);

	long accept(Article storedArticle, long numerator, long divisor);

	boolean canSupply(Article targetArticle);

	long available(Article targetArticle, long units);

	long supply(Article targetArticle, long howMany, long units);
}
