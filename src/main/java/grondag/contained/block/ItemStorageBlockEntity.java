package grondag.contained.block;

import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.fermion.varia.Base32Namer;
import grondag.fluidity.api.storage.DiscreteStorage;
import grondag.fluidity.api.storage.DiscreteStorageSupplier;
import grondag.fluidity.base.storage.bulk.AbstractStorage;

public class ItemStorageBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity, DiscreteStorageSupplier, BlockEntityClientSerializable {
	public static String TAG_STORAGE = "storage";
	public static String TAG_LABEL = "label";

	protected final Supplier<DiscreteStorage> storageSupplier;
	protected DiscreteStorage storage;
	protected String label = "UNKNOWN";
	protected ItemStorageClientState clientState;

	public ItemStorageBlockEntity(BlockEntityType<? extends ItemStorageBlockEntity> type, Supplier<DiscreteStorage> storageSupplier, String labelRoot) {
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
	public DiscreteStorage getDiscreteStorage() {
		DiscreteStorage result = storage;

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
		tag.put(TAG_STORAGE, getDiscreteStorage().writeTag());
		tag.putString(TAG_LABEL, label);
		return tag;
	}

	public void fromContainerTag(CompoundTag tag) {
		label = tag.getString(TAG_LABEL);
		getDiscreteStorage().readTag(tag.getCompound(TAG_STORAGE));
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

	//	@Environment(EnvType.CLIENT)
	//	public void notifyServerPlayerWatching() {
	//		final long time = world.getTime();
	//
	//		// don't send more frequently than needed
	//		if (time >= nextPlayerUpdateMilliseconds) {
	//			//TODO: implement
	//			//            PacketHandler.CHANNEL.sendToServer(new PacketMachineStatusAddListener(this.pos));
	//			nextPlayerUpdateMilliseconds = time + ContainedConfig.keepaliveIntervalMilliseconds;
	//		}
	//	}
}
