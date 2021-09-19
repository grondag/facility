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
package grondag.facility.storage.bulk;

import java.util.function.Supplier;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
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
	public void fromClientTag(CompoundTag tag) {
		label = tag.getString(TAG_LABEL);

		final TankClientState clientState = clientState();
		clientState.level = tag.getFloat("usage");

		if(clientState.level == 0) {
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

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		tag.putString(TAG_LABEL, label);
		final float usage = (float) storage.usage();
		tag.putFloat("usage", usage);

		if(usage != 0 && !storage.isEmpty()) {
			tag.putInt("fluid", Registry.FLUID.getId(storage.view(0).article().toFluid()));
		}

		return tag;
	}

	@Override
	protected void markForSave() {
		super.markForSave();

		if(level != null && worldPosition != null) {
			// PERF: gate this somehow?
			sync();
		}
	}
}
