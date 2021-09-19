package grondag.facility.transport.storage;

import grondag.fluidity.api.article.Article;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

public abstract class SidedInventoryStorageContext extends InventoryStorageContext<WorldlyContainer> {
	protected final Direction targetFace;

	public SidedInventoryStorageContext(Direction targetFace) {
		this.targetFace = targetFace;
	}

	@Override
	public boolean prepareForTick() {
		if (super.prepareForTick() && inventory instanceof WorldlyContainer) {
			slots = inventory.getSlotsForFace(targetFace);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean canExtract(ItemStack stack, int slot) {
		return inventory.canTakeItemThroughFace(slot, stack, targetFace);
	}

	@Override
	protected boolean canPlaceInSlot(Article article, int slot) {
		return inventory.canPlaceItemThroughFace(slot, article.toStack(), targetFace);
	}

	@Override
	protected boolean canPlaceInSlot(ItemStack stack, int slot) {
		return inventory.canPlaceItemThroughFace(slot, stack, targetFace);
	}
}
