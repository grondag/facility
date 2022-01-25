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

package grondag.facility.transport.item;

import org.apache.commons.lang3.ObjectUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import grondag.facility.storage.TickableBlockEntity;
import grondag.facility.transport.PipeBlock;
import grondag.facility.transport.buffer.TransportBuffer;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.world.BlockTest;

public class ItemMoverBlock extends PipeBlock {
	@SuppressWarnings("rawtypes")
	public static final BlockTest ITEM_MOVER_JOIN_TEST = ctx -> {
		final BlockState fromState = ctx.fromBlockState();
		assert fromState.getBlock() instanceof ItemMoverBlock : "Mismatched  block in ItemMoverBlock join test";
		return fromState.getValue(XmProperties.FACE) == ctx.toFace() || canConnect(ctx);
	};

	public ItemMoverBlock(Block.Properties settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory, boolean hasGlow) {
		super(settings, beFactory, hasGlow);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.POWERED);
		builder.add(XmProperties.FACE);
	}

	@Override
	public void neighborChanged(BlockState blockState, Level world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		final boolean hasPower = world.hasNeighborSignal(blockPos);

		if (hasPower != blockState.getValue(BlockStateProperties.POWERED)) {
			world.setBlock(blockPos, blockState.setValue(BlockStateProperties.POWERED, hasPower), 3);
		}

		if (!world.isClientSide && BlockPos.offset(blockPos.asLong(), blockState.getValue(XmProperties.FACE)) == blockPos2.asLong()) {
			((ItemMoverBlockEntity) world.getBlockEntity(blockPos)).resetTickHandler = true;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context)
				.setValue(XmProperties.FACE, ObjectUtils.defaultIfNull(context.getClickedFace(), context.getNearestLookingDirection().getOpposite()).getOpposite())
				.setValue(BlockStateProperties.POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide) {
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof ItemMoverBlockEntity) {
				final TransportBuffer buffer = ((ItemMoverBlockEntity) be).transportBuffer;
				final ItemStack stack = buffer.flushItemToWorld();

				if (!stack.isEmpty()) {
					if (player.addItem(stack)) {
						player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
					} else {
						final ItemEntity itemEntity = player.drop(stack, false);

						if (itemEntity != null) {
							itemEntity.setNoPickUpDelay();
							itemEntity.setOwner(player.getUUID());
						}
					}
				}
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (!world.isClientSide && !state.is(newState.getBlock())) {
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof ItemMoverBlockEntity) {
				final TransportBuffer buffer = ((ItemMoverBlockEntity) be).transportBuffer;
				final ItemStack stack = buffer.flushItemToWorld();

				if (!stack.isEmpty()) {
					Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}

			world.updateNeighbourForOutputSignal(pos, this);
		}

		super.onRemove(state, world, pos, newState, moved);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world.isClientSide ? null : TickableBlockEntity::tick;
	}
}
