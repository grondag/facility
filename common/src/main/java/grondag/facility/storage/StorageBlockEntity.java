/*
 * This file is part of Facility and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.facility.storage;

import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.facility.block.CarrierSessionBlockEntity;
import grondag.facility.varia.Base32Namer;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.api.multiblock.MultiBlockMember;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.AbstractAggregateStore;
import grondag.fluidity.base.storage.AbstractStore;
import grondag.fluidity.base.storage.ForwardingStore;

@SuppressWarnings("rawtypes")
public abstract class StorageBlockEntity<T extends StorageClientState, U extends MultiBlockMember> extends CarrierSessionBlockEntity {
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
		if (level != null && worldPosition != null) {
			level.blockEntityChanged(worldPosition);
		}
	}

	protected boolean isRegistered = false;

	@Override
	@SuppressWarnings("unchecked")
	protected void onLoaded() {
		if (!isRegistered && hasLevel() && !level.isClientSide) {
			deviceManager().connect(member);
			isRegistered = true;
		} else {
			assert false : "detected duplicate loading.";
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onUnloaded() {
		if (isRegistered && hasLevel() && !level.isClientSide) {
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

	/** Do not call on client - will not crash but wastes memory. */
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
		((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	protected void saveAdditional(CompoundTag compoundTag) {
		super.saveAdditional(compoundTag);
		compoundTag.put(TAG_STORAGE, getInternalStorage().writeTag());
		compoundTag.putString(TAG_LABEL, label);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		// NB: called for updates so packet may have partial data

		if (tag.contains(TAG_LABEL)) {
			label = tag.getString(TAG_LABEL);
		}

		if (tag.contains(TAG_STORAGE)) {
			getInternalStorage().readTag(tag.getCompound(TAG_STORAGE));
		}
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		final CompoundTag result = new CompoundTag();
		result.putString(TAG_LABEL, label);
		return result;
	}

	public T clientState() {
		T result = clientState;

		if (result == null) {
			result = createClientState();
			clientState = result;
		}

		return result;
	}

	protected abstract T createClientState();
}
