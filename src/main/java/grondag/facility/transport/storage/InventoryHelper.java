package grondag.facility.transport.storage;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

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

	public static boolean canPlaceInSlot(ItemStack stack, Inventory inv, int slot) {
		if (!inv.isValid(slot, stack)) {
			return false;
		}

		final ItemStack targetStack = inv.getStack(slot);

		return targetStack.isEmpty() || canStacksCombine(targetStack, stack);
	}

	public static boolean canStacksCombine(ItemStack targetStack, ItemStack sourceStack) {
		return targetStack.getItem() == sourceStack.getItem()
				&&  targetStack.getCount() < targetStack.getMaxCount()
				&& ItemStack.areTagsEqual(targetStack, sourceStack);
	}
}
