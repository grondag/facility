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
package grondag.facility.transport;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import grondag.facility.block.FacilitySpeciesBlock;
import grondag.facility.storage.StorageBlock;
import grondag.fermion.modkeys.api.ModKeys;
import grondag.xm.api.block.XmBlockState;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.collision.CollisionDispatcher;
import grondag.xm.api.connect.species.Species;
import grondag.xm.api.connect.species.SpeciesMode;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.world.BlockTest;
import grondag.xm.api.connect.world.BlockTestContext;

public class PipeBlock extends FacilitySpeciesBlock {
	public final boolean hasGlow;

	public PipeBlock(Block.Properties settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory, boolean hasGlow) {
		super(settings, beFactory, SpeciesProperty.speciesForBlockType(PipeBlock.class));
		this.hasGlow = hasGlow;
	}

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST = ctx -> canConnect(ctx);

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST_WITH_AXIS = ctx -> {
		assert ctx.fromBlockState().getBlock() instanceof StraightPipeBlock : "Non-axis `from` block in JOIN_TEST_WITH_AXIS";

		final Direction toFace = ctx.toFace();
		return toFace != null && toFace.getAxis() == ctx.fromBlockState().getValue(XmProperties.AXIS);
	};

	public static boolean canConnect(BlockTestContext<?> ctx) {
		final BlockState fromState = ctx.fromBlockState();
		assert fromState.getBlock() instanceof PipeBlock : "Non-pipe `from` block in PipeBlock.canConnect";

		final BlockState toState = ctx.toBlockState();

		if(toState.isAir()) {
			return false;
		}

		if(toState.getBlock() instanceof PipeBlock) {
			return canConnectSelf(fromState, ctx.fromPos(), toState, ctx.toPos());
		} else {
			return toState.getBlock() instanceof StorageBlock;
		}
	}

	public static boolean canConnectSelf(BlockState fromState, BlockPos fromPos, BlockState toState, BlockPos toPos) {
		if(fromState.getValue(SpeciesProperty.SPECIES) != toState.getValue(SpeciesProperty.SPECIES)) {
			return false;
		}

		final Comparable<?> fromAxis = fromState.getValues().get(XmProperties.AXIS);
		final Comparable<?> toAxis = toState.getValues().get(XmProperties.AXIS);

		if(fromAxis == null) {
			if(toAxis == null) {
				// both are flexible
				return true;
			} else {
				// require to-axis point to the flexible one
				return toAxis == Direction.fromNormal(toPos.getX() - fromPos.getX(), toPos.getY() - fromPos.getY(), toPos.getZ() - fromPos.getZ()).getAxis();
			}
		} else { // have from axis
			if(toAxis == null) {
				// require from-axis point to the flexible one
				return fromAxis == Direction.fromNormal(toPos.getX() - fromPos.getX(), toPos.getY() - fromPos.getY(), toPos.getZ() - fromPos.getZ()).getAxis();
			} else {
				// both straight, require same axis
				return toAxis == fromAxis;
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos pos, CollisionContext entityContext) {
		return CollisionDispatcher.shapeFor(XmBlockState.modelState(blockState, blockView, pos, true));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockView, List<Component> list, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, blockView, list, tooltipContext);
		list.add(new TranslatableComponent("transport.facility.utb1").withStyle(ChatFormatting.GOLD));
		list.add(new TranslatableComponent("transport.facility.utb1.desc").withStyle(ChatFormatting.GOLD));

		final int species = PipeBlockItem.species(itemStack);

		if (species == PipeBlockItem.AUTO_SELECT_SPECIES) {
			list.add(new TranslatableComponent("transport.facility.circuit.auto").withStyle(ChatFormatting.AQUA));
		} else {
			list.add(new TranslatableComponent("transport.facility.circuit.num", species).withStyle(ChatFormatting.AQUA));
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		int species = PipeBlockItem.species(context.getItemInHand());

		if (species == PipeBlockItem.AUTO_SELECT_SPECIES) {
			final Direction face = context.getNearestLookingDirection();
			final BlockPos onPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
			final SpeciesMode mode = ModKeys.isPrimaryPressed(context.getPlayer()) ? SpeciesMode.COUNTER_MOST : SpeciesMode.MATCH_MOST;
			species = Species.speciesForPlacement(context.getLevel(), onPos, face.getOpposite(), mode, speciesFunc);
		}

		return defaultBlockState().setValue(SpeciesProperty.SPECIES, species);
	}
}
