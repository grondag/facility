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

	private static BlockEntityType<PipeBlockEntity> pipeBET_UTB1;

	private static PipeBlockEntity pipeSupplier(BlockPos pos, BlockState state) {
		return new PipeBlockEntity(pipeBET_UTB1, pos, state);
	}

	private static BlockEntityType<StorageToBusBlockEntity> pipeBET_UTB1_S2B;

	private static StorageToBusBlockEntity intakeSupplier(BlockPos pos, BlockState state) {
		return new StorageToBusBlockEntity(pipeBET_UTB1_S2B, pos, state);
	}

	private static BlockEntityType<BusToStorageBlockEntity> pipeBET_UTB1_B2S;

	private static BusToStorageBlockEntity exportSupplier(BlockPos pos, BlockState state) {
		return new BusToStorageBlockEntity(pipeBET_UTB1_B2S, pos, state);
	}

	public static void initialize() {
		final var pipeBlockUTB1 = Facility.block("utb1_flex", new PipeBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::pipeSupplier, false), PipeBlockItem::new);
		final var pipeBlockGlowUTB1 = Facility.block("utb1_flex_g", new PipeBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::pipeSupplier, true), PipeBlockItem::new);
		final var pipeBlockStraightUTB1 = Facility.block("utb1_straight", new StraightPipeBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::pipeSupplier, false), PipeBlockItem::new);
		final var pipeBlockStraightGlowUTB1 = Facility.block("utb1_straight_g", new StraightPipeBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::pipeSupplier, true), PipeBlockItem::new);
		pipeBET_UTB1 = Facility.blockEntityType("utb1", PipeBlocks::pipeSupplier, pipeBlockUTB1, pipeBlockStraightUTB1, pipeBlockGlowUTB1, pipeBlockStraightGlowUTB1);

		final var storage2BusBlockUTB1 = Facility.block("utb1_intake", new ItemMoverBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::intakeSupplier, false), PipeBlockItem::new);
		final var storage2BusBlockGlowUTB1 = Facility.block("utb1_intake_g", new ItemMoverBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::intakeSupplier, true), PipeBlockItem::new);
		pipeBET_UTB1_S2B = Facility.blockEntityType("utb1_intake", PipeBlocks::intakeSupplier, storage2BusBlockUTB1, storage2BusBlockGlowUTB1);

		final var bus2StorageBlockUTB1 = Facility.block("utb1_export", new ItemMoverBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::exportSupplier, false), PipeBlockItem::new);
		final var bus2StorageBlockGlowUTB1 = Facility.block("utb1_export_g", new ItemMoverBlock(Block.Properties.of(Material.METAL).dynamicShape().strength(1, 1), PipeBlocks::exportSupplier, true), PipeBlockItem::new);
		pipeBET_UTB1_B2S = Facility.blockEntityType("utb1_export", PipeBlocks::exportSupplier, bus2StorageBlockUTB1, bus2StorageBlockGlowUTB1);

		CarrierProvider.CARRIER_PROVIDER_COMPONENT.registerProvider(ctx -> ((PipeBlockEntity) ctx.blockEntity()).getCarrierProvider(ctx), pipeBlockUTB1, pipeBlockStraightUTB1, storage2BusBlockUTB1, bus2StorageBlockUTB1, pipeBlockGlowUTB1, pipeBlockStraightGlowUTB1, storage2BusBlockGlowUTB1, bus2StorageBlockGlowUTB1);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> ((ItemMoverBlockEntity) ctx.blockEntity()).getConsumer(), storage2BusBlockUTB1, bus2StorageBlockUTB1, storage2BusBlockGlowUTB1, bus2StorageBlockGlowUTB1);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> ((ItemMoverBlockEntity) ctx.blockEntity()).getSupplier(), storage2BusBlockUTB1, bus2StorageBlockUTB1, storage2BusBlockGlowUTB1, bus2StorageBlockGlowUTB1);

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

		XmBlockRegistry.addBlockStates(pipeBlockUTB1, utb1FlexFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
		XmBlockRegistry.addBlockStates(pipeBlockGlowUTB1, utb1FlexFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);

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

		XmBlockRegistry.addBlockStates(pipeBlockStraightUTB1, utb1StraightFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
		XmBlockRegistry.addBlockStates(pipeBlockStraightGlowUTB1, utb1StraightFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);

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

		XmBlockRegistry.addBlockStates(storage2BusBlockUTB1, utb1S2bFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
		XmBlockRegistry.addBlockStates(storage2BusBlockGlowUTB1, utb1S2bFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);

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

		XmBlockRegistry.addBlockStates(bus2StorageBlockUTB1, utb1B2sFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
		XmBlockRegistry.addBlockStates(bus2StorageBlockGlowUTB1, utb1B2sFunc, PipeBlockItem.PIPE_ITEM_MODEL_FUNCTION);
	}
}
