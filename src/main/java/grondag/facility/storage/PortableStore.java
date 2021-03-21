package grondag.facility.storage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import grondag.fluidity.api.device.ItemComponentContext;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.AbstractPortableStore;

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
	protected NbtCompound readTagFromStack(ItemStack stack) {
		return stack.getOrCreateSubTag("BlockEntityTag").getCompound(StorageBlockEntity.TAG_STORAGE);
	}

	@Override
	protected void writeTagToStack(ItemStack stack, NbtCompound tag) {
		if(isEmpty()) {
			stack.setTag(null);
		} else {
			stack.getOrCreateSubTag("BlockEntityTag").put(StorageBlockEntity.TAG_STORAGE, tag);
			writeDamage(stack, this);
		}
	}

	public static void writeDamage(ItemStack stack, Store store) {
		final int max = stack.getMaxDamage();
		stack.setDamage(store.isEmpty() ? 0 : (max - (int) (store.usage() * (max - 1))));
	}
}
