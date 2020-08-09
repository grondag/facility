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
	public void prepareForTick() {
		super.prepareForTick();
		slots = inventory.getAvailableSlots(targetFace);
	}

	@Override
	public boolean isValid() {
		return inventory != null && inventory instanceof SidedInventory;
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
