package grondag.facility.storage.bulk;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import grondag.facility.storage.StorageBlockEntity;
import grondag.fluidity.api.device.ItemComponentContext;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.base.storage.bulk.AbstractPortableTank;

public class PortableTank extends AbstractPortableTank {
	public PortableTank() {
		super();
	}

	public PortableTank(Fraction defaultCapacity, ItemComponentContext ctx) {
		super(defaultCapacity, ctx);
	}

	@Override
	protected CompoundTag readTagFromStack(ItemStack stack) {
		return stack.getOrCreateSubTag("BlockEntityTag").getCompound(StorageBlockEntity.TAG_STORAGE);
	}

	@Override
	protected void writeTagToStack(ItemStack stack, CompoundTag tag) {
		stack.getOrCreateSubTag("BlockEntityTag").put(StorageBlockEntity.TAG_STORAGE, tag);
	}
}
