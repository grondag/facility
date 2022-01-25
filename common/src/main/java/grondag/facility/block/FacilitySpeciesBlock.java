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
