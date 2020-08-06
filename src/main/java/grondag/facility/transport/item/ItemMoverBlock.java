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
package grondag.facility.transport.item;

import java.util.function.Supplier;

import org.apache.commons.lang3.ObjectUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import grondag.facility.transport.PipeBlock;
import grondag.xm.api.block.XmProperties;

public class ItemMoverBlock extends PipeBlock {
	public ItemMoverBlock(Block.Settings settings, Supplier<BlockEntity> beFactory, boolean hasGlow) {
		super(settings, beFactory, hasGlow);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(Properties.POWERED);
		builder.add(XmProperties.FACE);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		final boolean hasPower = world.isReceivingRedstonePower(blockPos);

		if (hasPower != blockState.get(Properties.POWERED)) {
			world.setBlockState(blockPos, blockState.with(Properties.POWERED, hasPower), 3);
		}

		if(!world.isClient && BlockPos.offset(blockPos.asLong(), blockState.get(XmProperties.FACE)) == blockPos2.asLong()) {
			((ItemMoverBlockEntity) world.getBlockEntity(blockPos)).resetTickHandler();
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(XmProperties.FACE, ObjectUtils.defaultIfNull(context.getSide(), context.getPlayerLookDirection().getOpposite()).getOpposite());
	}
}
