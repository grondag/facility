package grondag.contained.block;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.fermion.varia.Base32Namer;
import grondag.fluidity.api.storage.DiscreteStorage;
import grondag.fluidity.api.storage.DiscreteStorageSupplier;

public class ItemStorageBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity, DiscreteStorageSupplier, Tickable, BlockEntityClientSerializable {
	public static String TAG_STORAGE = "storage";
	public static String TAG_LABEL = "label";

	protected DiscreteStorage storage;
	protected String label = "UNKNOWN";

	public ItemStorageBlockEntity(BlockEntityType<ItemStorageBlockEntity> type, DiscreteStorage storage, String labelRoot) {
		super(type);
		this.storage = storage;
		label = labelRoot + Base32Namer.makeFilteredName(ThreadLocalRandom.current().nextLong());
	}

	@Override
	public Object getRenderAttachmentData() {
		return this;
	}

	@Override
	public DiscreteStorage getDiscreteStorage() {
		return storage;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		markDirty();
		sync();
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		return toContainerTag(tag);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		fromContainerTag(tag);
	}

	public CompoundTag toContainerTag(CompoundTag tag) {
		tag.put(TAG_STORAGE, getDiscreteStorage().writeTag());
		tag.putString(TAG_LABEL, label);
		return tag;
	}

	public void fromContainerTag(CompoundTag tag) {
		label = tag.getString(TAG_LABEL);
		getDiscreteStorage().readTag(tag.getCompound(TAG_STORAGE));
	}

	@Override
	public void tick() {
		//		final DiscreteStorage storage = getDiscreteStorage();
		//
		//		if(storage == null) {
		//			return;
		//		}
		//
		//		if(storage.count() >= storage.capacity()) {
		//			storage.clear();
		//		} else if(storage instanceof SimpleItemStorage){
		//			final SimpleItemStorage myStorage = (SimpleItemStorage) storage;
		//			final ThreadLocalRandom rand = ThreadLocalRandom.current();
		//
		//			final ItemStack stack = myStorage.getInvStack(rand.nextInt(myStorage.getInvSize()));
		//
		//			if(stack.isEmpty()) {
		//				final Item item = Registry.ITEM.getRandom(rand);
		//				storage.accept(item, 1, false);
		//			} else {
		//				storage.accept(stack.getItem(), 1, false);
		//			}
		//		} else {
		//			final ThreadLocalRandom rand = ThreadLocalRandom.current();
		//			final int handle = rand.nextInt(200);
		//
		//			if(handle < storage.handleCount()) {
		//				storage.accept(storage.view(handle).item(), 1, false);
		//			} else {
		//				final Item item = Registry.ITEM.getRandom(rand);
		//				storage.accept(item, 1, false);
		//			}
		//		}
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		label = tag.getString(TAG_LABEL);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		tag.putString(TAG_LABEL, label);
		return tag;
	}
}
