package grondag.facility.transport.storage;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.Store;

public abstract class FluidityStorageContext implements TransportStorageContext {
	int nextHandle = 0;

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
	public boolean hasContentPreference() {
		return !(store.isAggregate() || store.isEmpty());
	}

	@Override
	public void beginIterating() {
		nextHandle = 0;
	}

	@Override
	public boolean hasNext() {
		return nextHandle < store.handleCount();
	}

	@Override
	public StoredArticleView next() {
		return store.view(nextHandle++);
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
		return store.getSupplier().apply(article, numerator, divisor, true);
	}
}
