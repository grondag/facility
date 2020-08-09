package grondag.facility.transport.storage;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.base.article.StoredDiscreteArticle;

public abstract class InventoryStorageContext implements TransportStorageContext {
	int supplySlot = 0;
	Article lastSupplyArticle = Article.NOTHING;

	int acceptSlot = 0;
	Article lastAcceptArticle = Article.NOTHING;

	int nextSlot = 0;
	final StoredDiscreteArticle view = new StoredDiscreteArticle();
	protected Inventory inventory;

	protected abstract Inventory inventory();

	@Override
	public void prepareForTick() {
		inventory = inventory();
	}

	@Override
	public boolean isValid() {
		return inventory != null;
	}

	@Override
	public boolean canAccept(Article article) {
		return positionToAccept(article);
	}

	protected boolean positionToAccept(Article article) {
		if (article.isNothing()) {
			return false;
		}

		final Inventory inv = inventory;

		if (inv == null) {
			return false;
		}

		final int limit = inv.size();

		// restart position search from beginning if different article
		if (acceptSlot < limit && article.equals(lastAcceptArticle) && InventoryHelper.canPlaceInSlot(article, inv, acceptSlot)) {
			return true;
		}

		for (int i = 0; i < limit; ++i) {
			if (InventoryHelper.canPlaceInSlot(article, inv, i)) {
				lastAcceptArticle = article;
				acceptSlot = i;
				return true;
			}
		}

		lastAcceptArticle = Article.NOTHING;
		return false;
	}

	@Override
	public boolean hasContentPreference() {
		return false;
	}

	@Override
	public void beginIterating() {
		nextSlot = 0;
	}

	@Override
	public boolean hasNext() {
		return nextSlot < inventory.size();
	}

	@Override
	public StoredArticleView next() {
		return view.prepare(inventory.getStack(nextSlot), nextSlot++);
	}

	@Override
	public long unitsFor(Article article) {
		return 1;
	}

	@Override
	public long capacityFor(Article article, long divisor) {
		if (divisor != 1 || !positionToAccept(article)) {
			return 0;
		}

		final ItemStack stack = inventory.getStack(acceptSlot);

		assert article.matches(stack) || stack.isEmpty();

		return stack.getMaxCount() - stack.getCount();
	}

	@Override
	public long accept(Article article, long numerator, long divisor) {
		if (divisor != 1 || !positionToAccept(article)) {
			return 0;
		}

		final Inventory inv = inventory;
		final ItemStack stack = inventory.getStack(acceptSlot);

		assert article.matches(stack) || stack.isEmpty();

		final long count = Math.min(numerator, stack.getMaxCount() - stack.getCount());

		if(stack.isEmpty()) {
			inv.setStack(acceptSlot, article.toStack(count));
		} else {
			stack.increment((int) count);
		}

		// TODO: remove
		//		Facility.LOG.info(String.format("Accepted %d %s", count, article.toItem().getTranslationKey()));

		inv.markDirty();

		return count;
	}

	protected boolean positionToSupply(Article article) {
		final Inventory inv = inventory;

		if (inv == null) {
			return false;
		}

		final int limit = inv.size();

		// restart position search from beginning if different article
		if (supplySlot < limit && article.equals(lastSupplyArticle)) {
			final ItemStack stack = inv.getStack(supplySlot);

			if (!stack.isEmpty() && article.matches(stack)) {
				return true;
			}
		}

		for (int i = 0; i < limit; ++i) {
			final ItemStack stack = inv.getStack(i);

			if (!stack.isEmpty() && article.matches(stack)) {
				supplySlot = i;
				lastSupplyArticle = article;
				return true;
			}
		}

		lastSupplyArticle = Article.NOTHING;
		return false;
	}

	@Override
	public boolean canSupply(Article article) {
		return positionToSupply(article);
	}

	@Override
	public long available(Article article, long divisor) {
		if (divisor != 1 || !positionToSupply(article)) {
			return 0;
		}

		final Inventory inv = inventory;
		final ItemStack stack = inv.getStack(supplySlot);

		assert !stack.isEmpty() && article.matches(stack);

		return stack.getCount();
	}

	@Override
	public long supply(Article article, long numerator, long divisor) {
		if (divisor != 1 || !positionToSupply(article)) {
			return 0;
		}

		final Inventory inv = inventory;
		final ItemStack stack = inv.getStack(supplySlot);

		assert !stack.isEmpty() && article.matches(stack);

		final long count = Math.min(numerator, stack.getCount());

		stack.decrement((int) count);

		if (stack.isEmpty()) {
			inv.setStack(supplySlot, ItemStack.EMPTY);
		}

		// TODO: remove
		//		Facility.LOG.info(String.format("Supplied %d %s", count, article.toItem().getTranslationKey()));

		inv.markDirty();

		return count;
	}
}
