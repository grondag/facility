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
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import grondag.facility.transport.PipeBlock;
import grondag.facility.transport.buffer.TransportBuffer;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.world.BlockTest;

public class ItemMoverBlock extends PipeBlock {
	@SuppressWarnings("rawtypes")
	public static final BlockTest ITEM_MOVER_JOIN_TEST = ctx -> {
		final BlockState fromState = ctx.fromBlockState();
		assert fromState.getBlock() instanceof ItemMoverBlock : "Mismatched  block in ItemMoverBlock join test";
		return fromState.get(XmProperties.FACE) == ctx.toFace() || canConnect(ctx);
	};

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
		return super.getPlacementState(context)
				.with(XmProperties.FACE, ObjectUtils.defaultIfNull(context.getSide(), context.getPlayerLookDirection().getOpposite()).getOpposite())
				.with(Properties.POWERED, context.getWorld().isReceivingRedstonePower(context.getBlockPos()));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			final BlockEntity be = world.getBlockEntity(pos);

			if(be instanceof ItemMoverBlockEntity) {
				final TransportBuffer buffer = ((ItemMoverBlockEntity) be).transportBuffer;
				final ItemStack stack = buffer.flushItemToWorld();

				if (!stack.isEmpty()) {
					if (player.giveItemStack(stack)) {
						player.world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
					} else {
						final ItemEntity itemEntity = player.dropItem(stack, false);
						if (itemEntity != null) {
							itemEntity.resetPickupDelay();
							itemEntity.setOwner(player.getUuid());
						}
					}
				}
			}
		}

		return ActionResult.SUCCESS;
	}
}
