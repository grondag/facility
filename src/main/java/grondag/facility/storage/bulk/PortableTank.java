package grondag.facility.storage.bulk;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import grondag.facility.storage.StorageBlockEntity;
import grondag.fluidity.api.device.ItemComponentContext;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.bulk.AbstractPortableTank;

public class PortableTank extends AbstractPortableTank {
	public PortableTank() {
		super();
	}

	public PortableTank(Fraction defaultCapacity, ItemComponentContext ctx) {
		super(defaultCapacity, ctx);
	}

	public PortableTank(Fraction defaultCapacity, java.util.function.Supplier<ItemStack> stackGetter, java.util.function.Consumer<ItemStack> stackSetter) {
		super(defaultCapacity, stackGetter, stackSetter);
	}

	@Override
	protected CompoundTag readTagFromStack(ItemStack stack) {
		return stack.getOrCreateSubTag("BlockEntityTag").getCompound(StorageBlockEntity.TAG_STORAGE);
	}

	@Override
	protected void writeTagToStack(ItemStack stack, CompoundTag tag) {
		stack.getOrCreateSubTag("BlockEntityTag").put(StorageBlockEntity.TAG_STORAGE, tag);
		writeDamage(stack, this);
	}

	public static void writeDamage(ItemStack stack, Store store) {
		final int max = stack.getMaxDamage();
		stack.setDamage(max - (int) (store.usage() * (max - 1)));
	}
}
