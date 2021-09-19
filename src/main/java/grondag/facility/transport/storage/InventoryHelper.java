package grondag.facility.transport.storage;

import grondag.fluidity.api.article.Article;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public abstract class InventoryHelper {
	private InventoryHelper() {}

	public static boolean isFull(Container inv) {
		final int limit = inv.getContainerSize();

		for (int i = 0; i < limit; ++i) {
			final ItemStack itemStack = inv.getItem(i);

			if (itemStack.isEmpty() || itemStack.getCount() < itemStack.getMaxStackSize()) {
				return false;
			}
		}

		return true;
	}

	public static boolean canPlaceInSlot(Article article, Container inv, int slot) {
		final ItemStack targetStack = inv.getItem(slot);

		if(targetStack.isEmpty()) {
			return (inv.canPlaceItem(slot, article.toStack()));
		}

		if (article.matches(targetStack) && targetStack.getCount() < targetStack.getMaxStackSize() && inv.canPlaceItem(slot, targetStack)) {
			return true;
		}

		return false;
	}

	public static boolean canPlaceInSlot(ItemStack stack, Container inv, int slot) {
		final ItemStack targetStack = inv.getItem(slot);

		if(targetStack.isEmpty()) {
			return inv.canPlaceItem(slot, stack);
		}

		if (canStacksCombine(targetStack, stack) && inv.canPlaceItem(slot, targetStack)) {
			return true;
		}

		return false;
	}

	public static boolean canStacksCombine(ItemStack targetStack, ItemStack sourceStack) {
		return targetStack.getItem() == sourceStack.getItem()
				&&  targetStack.getCount() < targetStack.getMaxStackSize()
				&& ItemStack.tagMatches(targetStack, sourceStack);
	}
}
