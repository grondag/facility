package grondag.facility.transport.storage;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.Store;

public abstract class FluidityStorageContext implements TransportStorageContext {
	int nextSupplyHandle = 0;

	Article lastSupply = Article.NOTHING;

	protected Store store;

	protected abstract Store store();

	@Override
	public void prepareForTick() {
		store = store();
	}

	@Override
	public boolean isValid() {
		return store.isValid();
	}

	@Override
	public boolean canAccept(Article targetArticle) {
		return store.getConsumer().canApply(targetArticle);
	}

	@Override
	public Article proposeSupply(ArticleType<?> type) {
		if (store.isEmpty()) {
			return Article.NOTHING;
		}

		if (lastSupply.isNothing() || lastSupply.type() != type || !store.getSupplier().canApply(lastSupply)) {
			// can't reuse last result so iterate content
			final int limit = store.handleCount();

			if (nextSupplyHandle >= limit) {
				nextSupplyHandle = 0;
			}

			final StoredArticleView v = store.view(nextSupplyHandle++);
			lastSupply = v.article().type() == type && !v.isEmpty() ? v.article() : Article.NOTHING;
		}

		return lastSupply;
	}

	@Override
	public Article proposeAccept(ArticleType<?> type) {
		// Fluidity stores will accept anything by default
		return store.isFull() ? null : Article.NOTHING;
	}

	@Override
	public void advanceAcceptProposal(ArticleType<?> articleType) {
		// NOOP
	}

	@Override
	public long unitsFor(Article article) {
		return article.type().isBulk() ? 1000 : 1;
	}

	@Override
	public long capacityFor(Article article, long divisor) {
		return store.getConsumer().apply(article, Long.MAX_VALUE, divisor, true);
	}

	@Override
	public long accept(Article article, long numerator, long divisor) {
		return store.getConsumer().apply(article, numerator, divisor, false);
	}

	@Override
	public boolean canSupply(Article article) {
		return store.getSupplier().canApply(article);
	}

	@Override
	public long available(Article article, long divisor) {
		return store.getSupplier().apply(article, Long.MAX_VALUE, divisor, true);
	}

	@Override
	public long supply(Article article, long numerator, long divisor) {
		return store.getSupplier().apply(article, numerator, divisor, false);
	}
}
