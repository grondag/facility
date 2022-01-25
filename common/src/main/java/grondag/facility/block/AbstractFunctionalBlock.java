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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class AbstractFunctionalBlock extends FacilityBlock {
	public AbstractFunctionalBlock(Properties settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory) {
		super(settings, beFactory);
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor iWorld, BlockPos blockPos, BlockPos blockPos2) {
		updateBe(iWorld, blockPos);
		return blockState;
	}

	@Override
	public void onPlace(BlockState blockState, Level world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		updateBe(world, blockPos);
		super.onPlace(blockState, world, blockPos, blockState2, bl);
	}

	@SuppressWarnings("rawtypes")
	protected void updateBe(LevelAccessor world, BlockPos pos) {
		final BlockEntity be = world.getBlockEntity(pos);

		if (be instanceof NeighboredBlockEntity) {
			((NeighboredBlockEntity) be).updateNeighbors();
		}
	}
}
