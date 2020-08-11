package grondag.facility.transport.storage;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import grondag.fluidity.api.article.Article;

public abstract class SidedInventoryStorageContext extends InventoryStorageContext<SidedInventory> {
	protected final Direction targetFace;

	public SidedInventoryStorageContext(Direction targetFace) {
		this.targetFace = targetFace;
	}

	@Override
	public boolean prepareForTick() {
		if (super.prepareForTick() && inventory instanceof SidedInventory) {
			slots = inventory.getAvailableSlots(targetFace);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean canExtract(ItemStack stack, int slot) {
		return inventory.canExtract(slot, stack, targetFace);
	}

	@Override
	protected boolean canPlaceInSlot(Article article, int slot) {
		return inventory.canInsert(slot, article.toStack(), targetFace);
	}

	@Override
	protected boolean canPlaceInSlot(ItemStack stack, int slot) {
		return inventory.canInsert(slot, stack, targetFace);
	}
}
