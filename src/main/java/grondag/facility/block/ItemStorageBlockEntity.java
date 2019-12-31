package grondag.facility.block;

import java.util.Set;
import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.facility.wip.transport.CarrierProvider;
import grondag.facility.wip.transport.CarrierSession;
import grondag.fermion.varia.Base32Namer;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.Authorization;
import grondag.fluidity.api.device.ComponentType;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.storage.AbstractStorage;
import grondag.fluidity.base.storage.ForwardingStorage;

public class ItemStorageBlockEntity extends CarrierSessionBlockEntity implements RenderAttachmentBlockEntity, BlockEntityClientSerializable {

	public static String TAG_STORAGE = "storage";
	public static String TAG_LABEL = "label";

	protected final Supplier<Storage> storageSupplier;
	protected Storage storage;
	protected final ForwardingStorage wrapper = new ForwardingStorage();
	protected String label = "UNKNOWN";
	protected ItemStorageClientState clientState;
	protected final ItemStorageMultiBlock.Member member;

	public ItemStorageBlockEntity(BlockEntityType<? extends ItemStorageBlockEntity> type, Supplier<Storage> storageSupplier, String labelRoot) {
		super(type);
		this.storageSupplier = storageSupplier;
		label = labelRoot + Base32Namer.makeFilteredName(ThreadLocalRandom.current().nextLong());
		member = new ItemStorageMultiBlock.Member(this, b -> b.getInternalStorage());
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
	public Storage getInternalStorage() {
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

	protected void markForSave() {
		if(world != null && pos != null) {
			world.markDirty(pos, this);
		}
	}

	protected boolean isRegistered = false;

	protected void registerDevice() {
		if(!isRegistered && hasWorld() && !world.isClient) {
			ItemStorageMultiBlock.DEVICE_MANAGER.connect(member);
			isRegistered = true;
		}
	}

	protected void unregisterDevice() {
		if(isRegistered && hasWorld() && !world.isClient) {
			ItemStorageMultiBlock.DEVICE_MANAGER.disconnect(member);
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

	@Override
	public Set<ArticleType<?>> articleTypes() {
		return ArticleType.SET_OF_ITEMS;
	}

	@Override
	protected CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.applyIfPresent(be, neighborSide, p ->
		p.attachIfPresent(ArticleType.ITEM, this, wrapper::getConsumer, wrapper::getSupplier));
	}

	@Override
	protected <T> T getOtherComponent(ComponentType<T> serviceType, Authorization auth, Direction side, Identifier id) {
		if(serviceType == Storage.STORAGE_COMPONENT) {
			if(wrapper.getWrapped() == Storage.EMPTY) {
				wrapper.setWrapped(getInternalStorage());
			}

			return serviceType.cast(wrapper);
		} else if(serviceType == Storage.INTERNAL_STORAGE_COMPONENT) {
			return serviceType.cast(getInternalStorage());
		} else {
			return serviceType.absent();
		}
	}
}
