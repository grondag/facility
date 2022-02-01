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

package grondag.facility.storage.bulk;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

import grondag.facility.storage.StorageBlockEntity;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.base.storage.AbstractStore;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;

public class TankBlockEntity extends StorageBlockEntity<TankClientState, TankMultiBlock.Member> {
	public TankBlockEntity(BlockEntityType<TankBlockEntity> type, BlockPos pos, BlockState state, @SuppressWarnings("rawtypes") Supplier<AbstractStore> storageSupplier, String labelRoot) {
		super(type, pos, state, storageSupplier, labelRoot);
	}

	@Override
	protected TankMultiBlock.Member createMember() {
		return new TankMultiBlock.Member(this, b -> b.getInternalStorage());
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected MultiBlockManager deviceManager() {
		return TankMultiBlock.DEVICE_MANAGER;
	}

	@Override
	protected CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.getAccess(be).applyIfPresent(neighborSide, p ->
		p.attachIfPresent(ArticleType.FLUID, ct -> ct.getAccess(this)));
	}

	@Override
	protected TankClientState createClientState() {
		return new TankClientState(this);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		if (level != null && level.isClientSide && tag.contains("usage")) {
			final TankClientState clientState = clientState();
			clientState.level = tag.getFloat("usage");

			if (clientState.level == 0) {
				clientState.fluidSprite = null;
			} else {
				final Fluid fluid = Registry.FLUID.byId(tag.getInt("fluid"));
				final FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);

				if (handler == null) {
					clientState.fluidSprite = null;
				} else {
					clientState.fluidColor = handler.getFluidColor(getLevel(), getBlockPos(), fluid.defaultFluidState());
					clientState.fluidSprite = handler.getFluidSprites(getLevel(), getBlockPos(), fluid.defaultFluidState())[0];
					clientState.glowing = fluid.defaultFluidState().createLegacyBlock().getLightEmission() > 0;
				}
			}
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
		final float usage = (float) storage.usage();
		result.putFloat("usage", usage);

		if (usage != 0 && !storage.isEmpty()) {
			result.putInt("fluid", Registry.FLUID.getId(storage.view(0).article().toFluid()));
		}

		return result;
	}

	@Override
	protected void markForSave() {
		super.markForSave();

		if (level != null && worldPosition != null) {
			// PERF: gate this somehow?
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
		}
	}
}
