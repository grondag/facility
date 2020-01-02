/*******************************************************************************
 * Copyright 2019, 2020 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.facility.storage;

import java.util.Set;
import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.facility.block.CarrierSessionBlockEntity;
import grondag.fermion.varia.Base32Namer;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.storage.AbstractStorage;
import grondag.fluidity.base.storage.ForwardingStorage;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;

public class CrateBlockEntity extends CarrierSessionBlockEntity implements RenderAttachmentBlockEntity, BlockEntityClientSerializable {

	public static final String TAG_STORAGE = "storage";
	public static final String TAG_LABEL = "label";

	protected final Storage storage;
	protected final ForwardingStorage wrapper = new ForwardingStorage();
	protected String label = "UNKNOWN";
	protected CrateClientState clientState;
	protected final CrateMultiBlock.Member member;

	@SuppressWarnings("rawtypes")
	public CrateBlockEntity(BlockEntityType<? extends CrateBlockEntity> type, Supplier<Storage> storageSupplier, String labelRoot) {
		super(type);
		storage = storageSupplier.get();
		((AbstractStorage) storage).onDirty(this::markForSave);
		wrapper.setWrapped(storage);
		label = labelRoot + Base32Namer.makeFilteredName(ThreadLocalRandom.current().nextLong());
		member = new CrateMultiBlock.Member(this, b -> b.getInternalStorage());
	}

	@Override
	public Object getRenderAttachmentData() {
		return this;
	}

	public CrateClientState clientState() {
		CrateClientState result = clientState;

		if (result == null) {
			result = new CrateClientState(this);
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
	public Storage getInternalStorage() {
		return storage;
	}

	public Storage getEffectiveStorage() {
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
			CrateMultiBlock.DEVICE_MANAGER.connect(member);
			isRegistered = true;
		}
	}

	protected void unregisterDevice() {
		if(isRegistered && hasWorld() && !world.isClient) {
			CrateMultiBlock.DEVICE_MANAGER.disconnect(member);
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
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.get(be).applyIfPresent(neighborSide, p ->
		p.attachIfPresent(ArticleType.ITEM, this, ct -> ct.get(this)));
	}
}
