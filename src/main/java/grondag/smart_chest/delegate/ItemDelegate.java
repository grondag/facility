package grondag.smart_chest.delegate;

import net.minecraft.item.ItemStack;

import grondag.fermion.gui.container.ItemDisplayDelegate;

public class ItemDelegate implements ItemDisplayDelegate {
	public ItemStack stack;
	public long count;
	public int handle;

	public ItemDelegate(ItemStack stack, long count, int handle) {
		prepare(stack, count, handle);
	}

	public ItemDelegate prepare (ItemStack stack, long count, int handle) {
		this.stack = stack;
		this.count = count;
		this.handle = handle;
		return this;
	}

	@Override
	public int handle() {
		return handle;
	}

	@Override
	public ItemStack displayStack() {
		return stack;
	}

	@Override
	public long count() {
		return count;
	}
}
