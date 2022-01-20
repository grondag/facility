package grondag.facility.storage;

import grondag.fluidity.api.device.ItemComponentContext;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.AbstractPortableStore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class PortableStore extends AbstractPortableStore {
	public PortableStore(Store wrapped) {
		super(wrapped);
	}

	public PortableStore(Store wrapped, ItemComponentContext ctx) {
		super(wrapped, ctx);
	}

	public PortableStore(Store wrapped, java.util.function.Supplier<ItemStack> stackGetter, java.util.function.Consumer<ItemStack> stackSetter) {
		super(wrapped, stackGetter, stackSetter);
	}

	@Override
	protected CompoundTag readTagFromStack(ItemStack stack) {
		return stack.getOrCreateTagElement("BlockEntityTag").getCompound(StorageBlockEntity.TAG_STORAGE);
	}

	@Override
	protected void writeTagToStack(ItemStack stack, CompoundTag tag) {
		if(isEmpty()) {
			stack.setTag(null);
		} else {
			stack.getOrCreateTagElement("BlockEntityTag").put(StorageBlockEntity.TAG_STORAGE, tag);
			writeDamage(stack, this);
		}
	}

	public static void writeDamage(ItemStack stack, Store store) {
		final int max = stack.getMaxDamage();
		stack.setDamageValue(store.isEmpty() ? 0 : (max - (int) (store.usage() * (max - 1))));
	}
}
