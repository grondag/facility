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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import grondag.facility.Facility;
import grondag.facility.block.FacilitySpeciesBlock;
import grondag.facility.block.NeighboredBlockEntity;
import grondag.fermion.modkeys.api.ModKeys;
import grondag.fluidity.api.storage.Store;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;

public abstract class StorageBlock extends FacilitySpeciesBlock {
	public static final ResourceLocation CONTENTS  = ShulkerBoxBlock.CONTENTS;

	public StorageBlock(Block.Properties settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory) {
		super(settings, beFactory, SpeciesProperty.speciesForBlockType(StorageBlock.class));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(XmProperties.FACE);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState blockState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos blockPos) {
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
	public void playerWillDestroy(Level world, BlockPos blockPos, BlockState blockState, Player playerEntity) {
		// Drop in creative mode
		if (playerEntity.isCreative()) {
			final BlockEntity blockEntity = world.getBlockEntity(blockPos);

			if (blockEntity instanceof StorageBlockEntity) {
				if (!world.isClientSide) {
					final ItemStack stack = getDropStack((StorageBlockEntity<?, ?>) blockEntity);
					final ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
					itemEntity.setDefaultPickUpDelay();
					world.addFreshEntity(itemEntity);
				}
			}
		}

		super.playerWillDestroy(world, blockPos, blockState, playerEntity);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		final BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

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
				stack.addTagElement("BlockEntityTag", tag);
			}

			stack.setHoverName(new TextComponent(myBlockEntity.getLabel()));
			writeCustomStackData(stack, myBlockEntity.getInternalStorage());
		}

		return stack;
	}

	protected void writeCustomStackData(ItemStack stack, Store store) {

	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(XmProperties.FACE,
				ModKeys.isSecondaryPressed(context.getPlayer()) && context.getClickedFace() != null ? context.getClickedFace().getOpposite() : context.getNearestLookingDirection());
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

		if(be instanceof NeighboredBlockEntity) {
			((NeighboredBlockEntity) be).updateNeighbors();
		}
	}

	protected ItemStack getStack(boolean isEmpty) {
		return new ItemStack(this);
	}
}
