package grondag.facility.storage;

import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.facility.block.CarrierSessionBlockEntity;
import grondag.fermion.varia.Base32Namer;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.api.multiblock.MultiBlockMember;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.AbstractAggregateStore;
import grondag.fluidity.base.storage.AbstractStore;
import grondag.fluidity.base.storage.ForwardingStore;

@SuppressWarnings("rawtypes")
public abstract class StorageBlockEntity<T extends StorageClientState, U extends MultiBlockMember> extends CarrierSessionBlockEntity implements RenderAttachmentBlockEntity, BlockEntityClientSerializable  {
	public static final String TAG_STORAGE = "storage";
	public static final String TAG_LABEL = "label";

	protected final AbstractStore storage;
	public final ForwardingStore wrapper = new ForwardingStore();
	protected String label = "UNKNOWN";
	protected T clientState;
	protected final U member;

	public StorageBlockEntity(BlockEntityType<? extends StorageBlockEntity> type, BlockPos pos, BlockState state, Supplier<AbstractStore> storageSupplier, String labelRoot) {
		super(type, pos, state);
		storage = storageSupplier.get();
		storage.onDirty(this::markForSave);
		wrapper.setWrapped(storage);
		label = labelRoot + Base32Namer.makeFilteredName(ThreadLocalRandom.current().nextLong());
		member = createMember();
	}

	protected abstract U createMember();

	protected abstract MultiBlockManager deviceManager();

	protected void markForSave() {
		if(level != null && worldPosition != null) {
			level.blockEntityChanged(worldPosition);
		}
	}

	protected boolean isRegistered = false;

	@Override
	@SuppressWarnings("unchecked")
	public void onLoaded() {
		if(!isRegistered && hasLevel() && !level.isClientSide) {
			deviceManager().connect(member);
			isRegistered = true;
		} else {
			assert false : "detected duplicate loading.";
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onUnloaded() {
		if(isRegistered && hasLevel() && !level.isClientSide) {
			// device manager happens later, so remove from aggregate right away to avoid possibility of duping
			if (wrapper.getWrapped().isAggregate()) {
				((AbstractAggregateStore) wrapper.getWrapped()).removeStore(storage);
			}

			// aggregate store should not get this because we removed from it
			// above and it should no longer be listening
			storage.disconnect();

			deviceManager().disconnect(member);

			isRegistered = false;
		} else {
			assert false : "detected incorrected unloading.";
		}
	}

	/** Do not call on client - will not crash but wastes memory */
	public Store getInternalStorage() {
		return storage;
	}

	public Store getEffectiveStorage() {
		return wrapper;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		setChanged();
		sync();
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		return toContainerTag(tag);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		fromContainerTag(tag);
	}

	public CompoundTag toContainerTag(CompoundTag tag) {
		tag.put(TAG_STORAGE, getInternalStorage().writeTag());
		tag.putString(TAG_LABEL, label);
		return tag;
	}

	public void fromContainerTag(CompoundTag tag) {
		label = tag.getString(TAG_LABEL);
		getInternalStorage().readTag(tag.getCompound(TAG_STORAGE));
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

	@Override
	public Object getRenderAttachmentData() {
		return this;
	}

	public T clientState() {
		T result = clientState;

		if (result == null) {
			result = createClientState();
			clientState =  result;
		}

		return result;
	}

	protected abstract T createClientState();
}
