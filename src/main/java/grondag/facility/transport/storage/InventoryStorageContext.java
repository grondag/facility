package grondag.facility.transport.storage;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import grondag.facility.Facility;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.base.article.StoredDiscreteArticle;

public abstract class InventoryStorageContext implements TransportStorageContext {
	int targetSlot = 0;
	ItemStack targetStack = ItemStack.EMPTY;
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
		final Inventory inv = inventory;
		final int limit = inv.size();

		if (!article.matches(targetStack)) {
			targetStack = article.toStack();
		}

		final ItemStack stack = targetStack;

		if (targetSlot < limit && InventoryHelper.canPlaceInSlot(stack, inv, targetSlot)) {
			return true;
		}

		for (int i = 0; i < limit; ++i) {
			if (InventoryHelper.canPlaceInSlot(stack, inv, i)) {
				targetSlot = i;
				return true;
			}
		}

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

		assert article.matches(targetStack);

		return targetStack.getMaxCount() - inventory.getStack(targetSlot).getCount();
	}

	@Override
	public long accept(Article article, long numerator, long divisor) {
		if (divisor != 1 || !positionToAccept(article)) {
			return 0;
		}

		assert article.matches(targetStack);

		final Inventory inv = inventory;
		final ItemStack stack = inv.getStack(targetSlot);
		final long count = Math.min(numerator, targetStack.getMaxCount() - stack.getCount());

		if(stack.isEmpty()) {
			inv.setStack(targetSlot, article.toStack(count));
		} else {
			stack.increment((int) count);
		}

		// TODO: remove
		Facility.LOG.info(String.format("Accepted %d %s", count, article.toItem().getTranslationKey()));

		inv.markDirty();

		return count;
	}

	protected boolean positionToSupply(Article article) {
		final Inventory inv = inventory;
		final int limit = inv.size();

		if (targetSlot < limit) {
			final ItemStack stack = inv.getStack(targetSlot);

			if (article.matches(stack) && !stack.isEmpty()) {
				return true;
			}
		}

		for (int i = 0; i < limit; ++i) {
			final ItemStack stack = inv.getStack(i);

			if (article.matches(stack) && !stack.isEmpty()) {
				targetSlot = i;
				return true;
			}
		}

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
		final ItemStack stack = inv.getStack(targetSlot);

		assert article.matches(stack);

		return stack.getCount();
	}

	@Override
	public long supply(Article article, long numerator, long divisor) {
		if (divisor != 1 || !positionToSupply(article)) {
			return 0;
		}

		final Inventory inv = inventory;
		final ItemStack stack = inv.getStack(targetSlot);

		assert article.matches(stack);

		final long count = Math.min(numerator, stack.getCount());

		stack.decrement((int) count);

		if (stack.isEmpty()) {
			inv.setStack(targetSlot, ItemStack.EMPTY);
		}

		// TODO: remove
		Facility.LOG.info(String.format("Supplied %d %s", count, article.toItem().getTranslationKey()));

		inv.markDirty();

		return count;
	}
}
