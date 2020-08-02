package grondag.facility.storage;

import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.facility.block.BlockEntityUnloadCallback;
import grondag.facility.block.CarrierSessionBlockEntity;
import grondag.fermion.varia.Base32Namer;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.api.multiblock.MultiBlockMember;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.AbstractStore;
import grondag.fluidity.base.storage.ForwardingStore;

@SuppressWarnings("rawtypes")
public abstract class StorageBlockEntity<T extends StorageClientState, U extends MultiBlockMember> extends CarrierSessionBlockEntity implements BlockEntityUnloadCallback, RenderAttachmentBlockEntity, BlockEntityClientSerializable  {
	public static final String TAG_STORAGE = "storage";
	public static final String TAG_LABEL = "label";

	protected final AbstractStore storage;
	public final ForwardingStore wrapper = new ForwardingStore();
	protected String label = "UNKNOWN";
	protected T clientState;
	protected final U member;

	public StorageBlockEntity(BlockEntityType<? extends StorageBlockEntity> type, Supplier<AbstractStore> storageSupplier, String labelRoot) {
		super(type);
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
			world.markDirty(pos, this);
		}
	}

	protected boolean isRegistered = false;

	@SuppressWarnings("unchecked")
	protected void registerDevice() {
		if(!isRegistered && hasWorld() && !world.isClient) {
			deviceManager().connect(member);
			isRegistered = true;
		}
	}

	@SuppressWarnings("unchecked")
	protected void unregisterDevice() {
		if(isRegistered && hasWorld() && !world.isClient) {
			deviceManager().disconnect(member);
			isRegistered = false;
		}
	}

	@Override
	public void setLocation(World world, BlockPos blockPos) {
		unregisterDevice();
		super.setLocation(world, blockPos);
		registerDevice();
	}

	@Override
	public void markRemoved() {
		unregisterDevice();
		super.markRemoved();
	}

	@Override
	public void onBlockEntityUnloaded() {
		unregisterDevice();
	}

	@Override
	public void cancelRemoval() {
		super.cancelRemoval();
		registerDevice();
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
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		return toContainerTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
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

	/**
	 * Rely on the fact that BE render dispath will call this each frame
	 * and check for deltas to know if we should recompute distance.
	 * Avoids checking/recomputing in block entity renderer.
	 *
	 * PERF: still any benefit to this because BERD computes anyway.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public double getSquaredRenderDistance() {
		if (world.isClient) {
			final BlockPos pos = this.pos;
			clientState().updateLastDistanceSquared(BlockEntityRenderDispatcher.INSTANCE.camera.getPos().squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()));
		}

		return super.getSquaredRenderDistance();
	}
}
