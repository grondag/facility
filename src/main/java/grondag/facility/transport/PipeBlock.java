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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

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

	public PipeBlock(Block.Settings settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory, boolean hasGlow) {
		super(settings, beFactory, SpeciesProperty.speciesForBlockType(PipeBlock.class));
		this.hasGlow = hasGlow;
	}

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST = ctx -> canConnect(ctx);

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST_WITH_AXIS = ctx -> {
		assert ctx.fromBlockState().getBlock() instanceof StraightPipeBlock : "Non-axis `from` block in JOIN_TEST_WITH_AXIS";

		final Direction toFace = ctx.toFace();
		return toFace != null && toFace.getAxis() == ctx.fromBlockState().get(XmProperties.AXIS);
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
		if(fromState.get(SpeciesProperty.SPECIES) != toState.get(SpeciesProperty.SPECIES)) {
			return false;
		}

		final Comparable<?> fromAxis = fromState.getEntries().get(XmProperties.AXIS);
		final Comparable<?> toAxis = toState.getEntries().get(XmProperties.AXIS);

		if(fromAxis == null) {
			if(toAxis == null) {
				// both are flexible
				return true;
			} else {
				// require to-axis point to the flexible one
				return toAxis == Direction.fromVector(toPos.getX() - fromPos.getX(), toPos.getY() - fromPos.getY(), toPos.getZ() - fromPos.getZ()).getAxis();
			}
		} else { // have from axis
			if(toAxis == null) {
				// require from-axis point to the flexible one
				return fromAxis == Direction.fromVector(toPos.getX() - fromPos.getX(), toPos.getY() - fromPos.getY(), toPos.getZ() - fromPos.getZ()).getAxis();
			} else {
				// both straight, require same axis
				return toAxis == fromAxis;
			}
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos pos, ShapeContext entityContext) {
		return CollisionDispatcher.shapeFor(XmBlockState.modelState(blockState, blockView, pos, true));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
		super.appendTooltip(itemStack, blockView, list, tooltipContext);
		list.add(new TranslatableText("transport.facility.utb1").formatted(Formatting.GOLD));
		list.add(new TranslatableText("transport.facility.utb1.desc").formatted(Formatting.GOLD));

		final int species = PipeBlockItem.species(itemStack);

		if (species == PipeBlockItem.AUTO_SELECT_SPECIES) {
			list.add(new TranslatableText("transport.facility.circuit.auto").formatted(Formatting.AQUA));
		} else {
			list.add(new TranslatableText("transport.facility.circuit.num", species).formatted(Formatting.AQUA));
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		int species = PipeBlockItem.species(context.getStack());

		if (species == PipeBlockItem.AUTO_SELECT_SPECIES) {
			final Direction face = context.getPlayerLookDirection();
			final BlockPos onPos = context.getBlockPos().offset(context.getSide().getOpposite());
			final SpeciesMode mode = ModKeys.isPrimaryPressed(context.getPlayer()) ? SpeciesMode.COUNTER_MOST : SpeciesMode.MATCH_MOST;
			species = Species.speciesForPlacement(context.getWorld(), onPos, face.getOpposite(), mode, speciesFunc);
		}

		return getDefaultState().with(SpeciesProperty.SPECIES, species);
	}
}
