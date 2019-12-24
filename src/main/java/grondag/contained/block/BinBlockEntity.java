package grondag.contained.block;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import grondag.fluidity.api.article.DiscreteArticleView;
import grondag.fluidity.api.item.DiscreteItem;
import grondag.fluidity.api.storage.DiscreteStorage;

public class BinBlockEntity extends ItemStorageBlockEntity {
	protected final int divisionLevel;
	protected final DiscreteItem[] items;

	public BinBlockEntity(BlockEntityType<BinBlockEntity> type, Supplier<DiscreteStorage> storageSupplier, String labelRoot, int divisionLevel) {
		super(type, storageSupplier, labelRoot);
		this.divisionLevel = divisionLevel;
		items = new DiscreteItem[divisionLevel];
		Arrays.fill(items, DiscreteItem.NOTHING);
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		label = tag.getString(TAG_LABEL);

		boolean hasAny = false;
		final ItemStorageClientState clientState = clientState();
		ItemStack[] renderStacks = clientState.renderStacks;

		for(int i = 0; i < divisionLevel; i++) {

			final DiscreteItem newItem =  DiscreteItem.fromTag(tag, "i" + i);

			if(!Objects.equals(newItem, items[i])) {
				items[i] = newItem;
				hasAny = true;

				if(renderStacks == null) {
					renderStacks = new ItemStack[divisionLevel];
				}

				renderStacks[i] = newItem.toStack();
			}
		}

		clientState.renderStacks = hasAny ? renderStacks : null;
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		tag.putString(TAG_LABEL, label);

		for(int i = 0; i < divisionLevel; i++) {
			items[i].writeTag(tag, "i" + i);
		}

		return tag;
	}

	@Override
	public void setWorld(World world, BlockPos blockPos) {
		super.setWorld(world, blockPos);

		if(!world.isClient) {
			refreshClient();
		}
	}

	@Override
	protected void markForSave() {
		super.markForSave();

		if(world != null && pos != null) {
			refreshClient();
		}
	}

	protected void refreshClient() {
		boolean clientRefresh = false;
		final DiscreteStorage storage = getDiscreteStorage();

		for(int i = 0; i < divisionLevel; i++) {
			final DiscreteArticleView newView = storage.view(i);
			final DiscreteItem newItem = newView == null || newView.isEmpty() ? DiscreteItem.NOTHING : newView.item();

			if (!newItem.equals(items[i])) {
				items[i] = newItem;
				clientRefresh = true;
			}
		}

		if(clientRefresh) {
			sync();
		}
	}
}
