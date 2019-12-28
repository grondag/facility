package grondag.facility.block;

import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.facility.block.ItemStorageBlockEntity.ItemStorageMultiblock;
import grondag.fermion.varia.Base32Namer;
import grondag.fluidity.api.device.Device;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.storage.AbstractStorage;
import grondag.fluidity.base.storage.ForwardingStorage;
import grondag.fluidity.wip.CompoundDeviceManager;
import grondag.fluidity.wip.CompoundDeviceMember;
import grondag.fluidity.wip.CompoundDiscreteStorageDevice;

public class ItemStorageBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity, Device, BlockEntityClientSerializable, CompoundDeviceMember<ItemStorageBlockEntity, ItemStorageMultiblock> {
	protected static class ItemStorageMultiblock extends CompoundDiscreteStorageDevice<ItemStorageBlockEntity, ItemStorageMultiblock> {}

	protected static final CompoundDeviceManager<ItemStorageBlockEntity, ItemStorageMultiblock> DEVICE_MANAGER = new CompoundDeviceManager<>(
			ItemStorageMultiblock::new, (ItemStorageBlockEntity a, ItemStorageBlockEntity b) -> ItemStorageBlock.canConnect(a, b));

	public static String TAG_STORAGE = "storage";
	public static String TAG_LABEL = "label";

	protected final Supplier<Storage> storageSupplier;
	protected Storage storage;
	protected final ForwardingStorage wrapper = new ForwardingStorage();
	protected String label = "UNKNOWN";
	protected ItemStorageClientState clientState;

	public ItemStorageBlockEntity(BlockEntityType<? extends ItemStorageBlockEntity> type, Supplier<Storage> storageSupplier, String labelRoot) {
		super(type);
		this.storageSupplier = storageSupplier;
		label = labelRoot + Base32Namer.makeFilteredName(ThreadLocalRandom.current().nextLong());
	}

	@Override
	public Object getRenderAttachmentData() {
		return this;
	}

	public ItemStorageClientState clientState() {
		ItemStorageClientState result = clientState;

		if (result == null) {
			result = new ItemStorageClientState(this);
			clientState =  result;
		}

		return result;
	}


	/**
	 * Rely on the fact that BE render dispath will call this each frame
	 * and check for deltas to know if we should recompute distance.
	 * Avoids checking/recomputing in block entity renderer.
	 */
	@Override
	public double getSquaredDistance(double x, double y, double z) {
		if (world.isClient) {
			final double result = super.getSquaredDistance(x, y, z);
			clientState().updateLastDistanceSquared(result);
			return result;
		} else {
			return super.getSquaredDistance(x, y, z);
		}
	}

	/** Do not call on client - will not crash but wastes memory */
	@SuppressWarnings("rawtypes")
	@Override
	public Storage getLocalStorage() {
		Storage result = storage;

		if(result == null) {
			result = storageSupplier.get();
			((AbstractStorage) result).onDirty(this::markForSave);
			storage = result;
		}

		return result;
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
		tag.put(TAG_STORAGE, getLocalStorage().writeTag());
		tag.putString(TAG_LABEL, label);
		return tag;
	}

	public void fromContainerTag(CompoundTag tag) {
		label = tag.getString(TAG_LABEL);
		getLocalStorage().readTag(tag.getCompound(TAG_STORAGE));
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

	protected void markForSave() {
		if(world != null && pos != null) {
			world.markDirty(pos, this);
		}
	}

	@Override
	public long packedPos() {
		return pos.asLong();
	}

	@Override
	public int dimensionId() {
		return dimension().getRawId();
	}

	@Override
	public DimensionType dimension() {
		return world.getDimension().getType();
	}

	@Override
	public World world() {
		return world;
	}

	protected ItemStorageMultiblock owner = null;

	@Override
	public ItemStorageMultiblock getCompoundDevice() {
		return owner;
	}

	@Override
	public void setCompoundDevice(ItemStorageMultiblock owner) {
		this.owner = owner;

		if(owner == null) {
			wrapper.setWrapped(getLocalStorage());
		} else {
			wrapper.setWrapped(owner.getStorage());
		}
	}

	@Override
	public Storage getStorage() {
		if(wrapper.getWrapped() == Storage.EMPTY) {
			wrapper.setWrapped(getLocalStorage());
		}
		return wrapper;
	}

	protected boolean isRegistered = false;

	protected void registerDevice() {
		if(!isRegistered && hasWorld() && !world.isClient) {
			DEVICE_MANAGER.connect(this);
			isRegistered = true;
		}
	}

	protected void unregisterDevice() {
		if(isRegistered && hasWorld() && !world.isClient) {
			DEVICE_MANAGER.disconnect(this);
			isRegistered = false;
		}
	}

	@Override
	public void setWorld(World world, BlockPos blockPos) {
		unregisterDevice();
		super.setWorld(world, blockPos);
		registerDevice();
	}

	@Override
	public void markRemoved() {
		unregisterDevice();
		super.markRemoved();
	}

	@Override
	public void cancelRemoval() {
		super.cancelRemoval();
		registerDevice();
	}
}
