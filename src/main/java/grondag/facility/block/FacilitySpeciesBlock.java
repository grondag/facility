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
package grondag.facility.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import grondag.fermion.modkeys.api.ModKeys;
import grondag.xm.api.connect.species.Species;
import grondag.xm.api.connect.species.SpeciesFunction;
import grondag.xm.api.connect.species.SpeciesMode;
import grondag.xm.api.connect.species.SpeciesProperty;

public class FacilitySpeciesBlock extends FacilityBlock {
	protected final SpeciesFunction speciesFunc;

	public FacilitySpeciesBlock(Settings settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory, SpeciesFunction speciesFunc) {
		super(settings, beFactory);
		this.speciesFunc = speciesFunc;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(SpeciesProperty.SPECIES);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		final Direction face = context.getPlayerLookDirection();
		final BlockPos onPos = context.getBlockPos().offset(context.getSide().getOpposite());
		final SpeciesMode mode = ModKeys.isPrimaryPressed(context.getPlayer()) ? SpeciesMode.COUNTER_MOST : SpeciesMode.MATCH_MOST;
		final int species = Species.speciesForPlacement(context.getWorld(), onPos, face.getOpposite(), mode, speciesFunc);
		return getDefaultState().with(SpeciesProperty.SPECIES, species);
	}
}
