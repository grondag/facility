package grondag.facility.transport.storage;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import grondag.fluidity.api.article.Article;

public abstract class InventoryHelper {
	private InventoryHelper() {}

	public static boolean isFull(Inventory inv) {
		final int limit = inv.size();

		for (int i = 0; i < limit; ++i) {
			final ItemStack itemStack = inv.getStack(i);

			if (itemStack.isEmpty() || itemStack.getCount() < itemStack.getMaxCount()) {
				return false;
			}
		}

		return true;
	}

	public static boolean canPlaceInSlot(Article article, Inventory inv, int slot) {
		final ItemStack targetStack = inv.getStack(slot);

		if(targetStack.isEmpty()) {
			return (inv.isValid(slot, article.toStack()));
		}

		if (article.matches(targetStack) && targetStack.getCount() < targetStack.getMaxCount() && inv.isValid(slot, targetStack)) {
			return true;
		}

		return false;
	}

	public static boolean canPlaceInSlot(ItemStack stack, Inventory inv, int slot) {
		final ItemStack targetStack = inv.getStack(slot);

		if(targetStack.isEmpty()) {
			return inv.isValid(slot, stack);
		}

		if (canStacksCombine(targetStack, stack) && inv.isValid(slot, targetStack)) {
			return true;
		}

		return false;
	}

	public static boolean canStacksCombine(ItemStack targetStack, ItemStack sourceStack) {
		return targetStack.getItem() == sourceStack.getItem()
				&&  targetStack.getCount() < targetStack.getMaxCount()
				&& ItemStack.areNbtEqual(targetStack, sourceStack);
	}
}
