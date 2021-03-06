package grondag.facility.storage;

import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

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
		if(world != null && pos != null) {
			world.markDirty(pos);
		}
	}

	protected boolean isRegistered = false;

	@Override
	@SuppressWarnings("unchecked")
	public void onLoaded() {
		if(!isRegistered && hasWorld() && !world.isClient) {
			deviceManager().connect(member);
			isRegistered = true;
		} else {
			assert false : "detected duplicate loading.";
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onUnloaded() {
		if(isRegistered && hasWorld() && !world.isClient) {
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
		markDirty();
		sync();
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		return toContainerTag(tag);
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		fromContainerTag(tag);
	}

	public NbtCompound toContainerTag(NbtCompound tag) {
		tag.put(TAG_STORAGE, getInternalStorage().writeTag());
		tag.putString(TAG_LABEL, label);
		return tag;
	}

	public void fromContainerTag(NbtCompound tag) {
		label = tag.getString(TAG_LABEL);
		getInternalStorage().readTag(tag.getCompound(TAG_STORAGE));
	}

	@Override
	public void fromClientTag(NbtCompound tag) {
		label = tag.getString(TAG_LABEL);
	}

	@Override
	public NbtCompound toClientTag(NbtCompound tag) {
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
