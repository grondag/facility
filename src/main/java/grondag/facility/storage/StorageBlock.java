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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import grondag.facility.Facility;
import grondag.facility.block.FacilitySpeciesBlock;
import grondag.facility.block.NeighboredBlockEntity;
import grondag.fermion.modkeys.api.ModKeys;
import grondag.fluidity.api.storage.Store;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;

public abstract class StorageBlock extends FacilitySpeciesBlock {
	public static final Identifier CONTENTS  = ShulkerBoxBlock.CONTENTS;

	public StorageBlock(Block.Settings settings, Supplier<BlockEntity> beFactory) {
		super(settings, beFactory, SpeciesProperty.speciesForBlockType(StorageBlock.class));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(XmProperties.FACE);
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		final BlockEntity blockEntity = world.getBlockEntity(blockPos);

		if (blockEntity instanceof StorageBlockEntity) {
			@SuppressWarnings("rawtypes")
			final Store storage = ((StorageBlockEntity)blockEntity).getInternalStorage();

			if(storage != null){
				return (int)(Math.floor(14.0 * storage.usage())) + 1;
			}
		}

		return 0;
	}


	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		// Drop in creative mode
		if (playerEntity.isCreative()) {
			final BlockEntity blockEntity = world.getBlockEntity(blockPos);

			if (blockEntity instanceof StorageBlockEntity) {
				if (!world.isClient) {
					final ItemStack stack = getDropStack((StorageBlockEntity<?, ?>) blockEntity);
					final ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
					itemEntity.setToDefaultPickupDelay();
					world.spawnEntity(itemEntity);
				}
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		final BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);

		if (blockEntity == null || !(blockEntity instanceof StorageBlockEntity)) {
			Facility.LOG.error("Call to getDroppedStacks unable to retrieve block entity. Crashing to avoid loss of storage contents. This is usually a bug in another mod.");
		}

		final List<ItemStack> result = new ArrayList<>(1);
		result.add(getDropStack((StorageBlockEntity<?, ?>) blockEntity));
		return result;
	}

	private ItemStack getDropStack(StorageBlockEntity<?, ?> myBlockEntity) {
		final boolean isEmpty = myBlockEntity.getInternalStorage().isEmpty();

		final ItemStack stack = getStack(isEmpty);

		if(!isEmpty) {
			final CompoundTag tag = myBlockEntity.toContainerTag(new CompoundTag());

			if (!tag.isEmpty()) {
				stack.putSubTag("BlockEntityTag", tag);
			}

			stack.setCustomName(new LiteralText(myBlockEntity.getLabel()));
			writeCustomStackData(stack, myBlockEntity.getInternalStorage());
		}

		return stack;
	}

	protected void writeCustomStackData(ItemStack stack, Store store) {

	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(XmProperties.FACE,
				ModKeys.isSecondaryPressed(context.getPlayer()) && context.getSide() != null ? context.getSide().getOpposite() : context.getPlayerLookDirection());
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess iWorld, BlockPos blockPos, BlockPos blockPos2) {
		updateBe(iWorld, blockPos);
		return blockState;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		updateBe(world, blockPos);
		super.onBlockAdded(blockState, world, blockPos, blockState2, bl);
	}

	@SuppressWarnings("rawtypes")
	protected void updateBe(WorldAccess world, BlockPos pos) {
		final BlockEntity be = world.getBlockEntity(pos);

		if(be instanceof NeighboredBlockEntity) {
			((NeighboredBlockEntity) be).updateNeighbors();
		}
	}

	protected ItemStack getStack(boolean isEmpty) {
		return new ItemStack(this);
	}
}
