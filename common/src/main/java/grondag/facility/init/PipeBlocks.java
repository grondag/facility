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

package grondag.facility.init;

import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import grondag.facility.Facility;
import grondag.facility.transport.PipeBlock;
import grondag.facility.transport.PipeBlockEntity;
import grondag.facility.transport.PipeBlockItem;
import grondag.facility.transport.StraightPipeBlock;
import grondag.facility.transport.item.BusToStorageBlockEntity;
import grondag.facility.transport.item.ItemMoverBlock;
import grondag.facility.transport.item.ItemMoverBlockEntity;
import grondag.facility.transport.item.StorageToBusBlockEntity;
import grondag.facility.transport.model.BasePipeModel;
import grondag.facility.transport.model.ItemMoverModel;
import grondag.facility.transport.model.PipeModel;
import grondag.facility.transport.model.PipeModifiers;
import grondag.facility.transport.model.PipePaints;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.orientation.api.DirectionHelper;

@SuppressWarnings("unchecked")
public abstract class PipeBlocks {
	private PipeBlocks() { }

	private static PipeBlock UTB1_PIPE;
	private static PipeBlock UTB1_PIPE_GLOW;
	private static PipeBlock UTB1_STRAIGHT_PIPE;
	private static PipeBlock UTB1_STRAIGHT_PIPE_GLOW;
	private static BlockEntityType<PipeBlockEntity> UTB1_BLOCK_ENTITY_TYPE;
	private static PipeBlockEntity pipeSupplier(BlockPos pos, BlockState state) {
		return new PipeBlockEntity(UTB1_BLOCK_ENTITY_TYPE, pos, state);
	}

	private static ItemMoverBlock UTB1_S2B;
	private static ItemMoverBlock UTB1_S2B_GLOW;
	private static BlockEntityType<StorageToBusBlockEntity> UTB1_S2B_BLOCK_ENTITY_TYPE;
	private static StorageToBusBlockEntity intakeSupplier(BlockPos pos, BlockState state) {
		return new StorageToBusBlockEntity(UTB1_S2B_BLOCK_ENTITY_TYPE, pos, state);
	}

	private static ItemMoverBlock UTB1_B2S;
	private static ItemMoverBlock UTB1_B2S_GLOW;
	private static BlockEntityType<BusToStorageBlockEntity> UTB1_B2S_BLOCK_ENTITY_TYPE;
	private static BusToStorageBlockEntity exportSupplier(BlockPos pos, BlockState state) {
		return new BusToStorageBlockEntity(UTB1_B2S_BLOCK_ENTITY_TYPE, pos, state);
	}

	public static void initialize() {
		UTB1_PIPE = Facility.block("utb1_flex", new PipeBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::pipeSupplier, false), PipeBlockItem::new);
		UTB1_PIPE_GLOW = Facility.block("utb1_flex_g", new PipeBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::pipeSupplier, true), PipeBlockItem::new);
		UTB1_STRAIGHT_PIPE = Facility.block("utb1_straight", new StraightPipeBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::pipeSupplier, false), PipeBlockItem::new);
		UTB1_STRAIGHT_PIPE_GLOW = Facility.block("utb1_straight_g", new StraightPipeBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::pipeSupplier, true), PipeBlockItem::new);
		UTB1_BLOCK_ENTITY_TYPE = Facility.blockEntityType("utb1", PipeBlocks::pipeSupplier, UTB1_PIPE, UTB1_STRAIGHT_PIPE, UTB1_PIPE_GLOW, UTB1_STRAIGHT_PIPE_GLOW);

		UTB1_S2B = Facility.block("utb1_intake", new ItemMoverBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::intakeSupplier, false), PipeBlockItem::new);
		UTB1_S2B_GLOW = Facility.block("utb1_intake_g", new ItemMoverBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::intakeSupplier, true), PipeBlockItem::new);
		UTB1_S2B_BLOCK_ENTITY_TYPE = Facility.blockEntityType("utb1_intake", PipeBlocks::intakeSupplier, UTB1_S2B, UTB1_S2B_GLOW);

		UTB1_B2S = Facility.block("utb1_export", new ItemMoverBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::exportSupplier, false), PipeBlockItem::new);
		UTB1_B2S_GLOW = Facility.block("utb1_export_g", new ItemMoverBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::exportSupplier, true), PipeBlockItem::new);
		UTB1_B2S_BLOCK_ENTITY_TYPE = Facility.blockEntityType("utb1_export", PipeBlocks::exportSupplier, UTB1_B2S, UTB1_B2S_GLOW);

		CarrierProvider.CARRIER_PROVIDER_COMPONENT.registerProvider(ctx -> ((PipeBlockEntity) ctx.blockEntity()).getCarrierProvider(ctx), UTB1_PIPE, UTB1_STRAIGHT_PIPE, UTB1_S2B, UTB1_B2S, UTB1_PIPE_GLOW, UTB1_STRAIGHT_PIPE_GLOW, UTB1_S2B_GLOW, UTB1_B2S_GLOW);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> ((ItemMoverBlockEntity) ctx.blockEntity()).getConsumer(), UTB1_S2B, UTB1_B2S, UTB1_S2B_GLOW, UTB1_B2S_GLOW);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> ((ItemMoverBlockEntity) ctx.blockEntity()).getSupplier(), UTB1_S2B, UTB1_B2S, UTB1_S2B_GLOW, UTB1_B2S_GLOW);

		final Function<BlockState, PrimitiveStateFunction> utb1FlexFunc = bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModifiers.PIPE_CONNECTOR_UPDATE)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						PipeModel.PRIMITIVE.newState()
						.alternateJoinBits(0)
						.paint(BasePipeModel.SURFACE_CABLE, PipePaints.CABLE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_FACE, PipePaints.STD_CONNECTOR_FACE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_SIDE, PipePaints.STD_CONNECTOR_SIDE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_BACK, PipePaints.STD_CONNECTOR_BACK)
						.simpleJoin(SimpleJoinState.ALL_JOINS), bs)))
				.build();

		XmBlockRegistry.addBlockStates(UTB1_PIPE, utb1FlexFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
		XmBlockRegistry.addBlockStates(UTB1_PIPE_GLOW, utb1FlexFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);

		final Function<BlockState, PrimitiveStateFunction> utb1StraightFunc = bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST_WITH_AXIS)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModifiers.PIPE_CONNECTOR_UPDATE)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						PipeModel.PRIMITIVE.newState()
						.alternateJoinBits(0)
						.paint(BasePipeModel.SURFACE_CABLE, PipePaints.CABLE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_FACE, PipePaints.STD_CONNECTOR_FACE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_BACK, PipePaints.STD_CONNECTOR_BACK)
						.paint(BasePipeModel.SURFACE_CONNECTOR_SIDE, PipePaints.STD_CONNECTOR_SIDE)
						.simpleJoin(SimpleJoinState.Z_JOINS), bs)))
				.build();

		XmBlockRegistry.addBlockStates(UTB1_STRAIGHT_PIPE, utb1StraightFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
		XmBlockRegistry.addBlockStates(UTB1_STRAIGHT_PIPE_GLOW, utb1StraightFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);

		final Function<BlockState, PrimitiveStateFunction> utb1S2bFunc = bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemMoverBlock.ITEM_MOVER_JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModifiers.MOVER_CONNECTOR_UPDATE)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						ItemMoverModel.PRIMITIVE.newState()
						.alternateJoinBits(DirectionHelper.DOWN_BIT)
						.paint(BasePipeModel.SURFACE_CABLE, PipePaints.CABLE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_FACE, PipePaints.STD_CONNECTOR_FACE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_BACK, PipePaints.STD_CONNECTOR_BACK)
						.paint(BasePipeModel.SURFACE_CONNECTOR_SIDE, PipePaints.STD_CONNECTOR_SIDE)
						.paint(ItemMoverModel.SURFACE_MOVER_FACE, PipePaints.OUTPUT_CONNECTOR_FACE)
						.paint(ItemMoverModel.SURFACE_MOVER_BACK, PipePaints.OUTPUT_CONNECTOR_BACK)
						.paint(ItemMoverModel.SURFACE_MOVER_SIDE, PipePaints.OUTPUT_CONNECTOR_SIDE)
						.orientationIndex(Direction.DOWN.ordinal())
						.simpleJoin(SimpleJoinState.Y_JOINS), bs)))
				.build();

		XmBlockRegistry.addBlockStates(UTB1_S2B, utb1S2bFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
		XmBlockRegistry.addBlockStates(UTB1_S2B_GLOW, utb1S2bFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);

		final Function<BlockState, PrimitiveStateFunction> utb1B2sFunc = bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemMoverBlock.ITEM_MOVER_JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModifiers.MOVER_CONNECTOR_UPDATE)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						ItemMoverModel.PRIMITIVE.newState()
						.alternateJoinBits(DirectionHelper.UP_BIT)
						.paint(BasePipeModel.SURFACE_CABLE, PipePaints.CABLE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_FACE, PipePaints.STD_CONNECTOR_FACE)
						.paint(BasePipeModel.SURFACE_CONNECTOR_BACK, PipePaints.STD_CONNECTOR_BACK)
						.paint(BasePipeModel.SURFACE_CONNECTOR_SIDE, PipePaints.STD_CONNECTOR_SIDE)
						.paint(ItemMoverModel.SURFACE_MOVER_FACE, PipePaints.INPUT_CONNECTOR_FACE)
						.paint(ItemMoverModel.SURFACE_MOVER_BACK, PipePaints.INPUT_CONNECTOR_BACK)
						.paint(ItemMoverModel.SURFACE_MOVER_SIDE, PipePaints.INPUT_CONNECTOR_SIDE)
						.orientationIndex(Direction.UP.ordinal())
						.simpleJoin(SimpleJoinState.Y_JOINS), bs)))
				.build();

		XmBlockRegistry.addBlockStates(UTB1_B2S, utb1B2sFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
		XmBlockRegistry.addBlockStates(UTB1_B2S_GLOW, utb1B2sFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
	}
}
