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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import grondag.facility.Facility;
import grondag.xm.api.connect.species.Species;
import grondag.xm.api.connect.species.SpeciesFunction;
import grondag.xm.api.connect.species.SpeciesMode;
import grondag.xm.api.connect.species.SpeciesProperty;

public class FacilitySpeciesBlock extends FacilityBlock {
	protected final SpeciesFunction speciesFunc;

	public FacilitySpeciesBlock(Properties settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory, SpeciesFunction speciesFunc) {
		super(settings, beFactory);
		this.speciesFunc = speciesFunc;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SpeciesProperty.SPECIES);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final Direction face = context.getNearestLookingDirection();
		final BlockPos onPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
		final SpeciesMode mode = Facility.forceKey.isPressed(context.getPlayer()) ? SpeciesMode.COUNTER_MOST : SpeciesMode.MATCH_MOST;
		final int species = Species.speciesForPlacement(context.getLevel(), onPos, face.getOpposite(), mode, speciesFunc);
		return defaultBlockState().setValue(SpeciesProperty.SPECIES, species);
	}
}
