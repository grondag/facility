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
package grondag.facility.init;

import static grondag.facility.Facility.REG;

import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

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
import grondag.fermion.orientation.api.DirectionHelper;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;

@SuppressWarnings("unchecked")
public enum PipeBlocks {
	;

	public static final PipeBlock UTB1_PIPE = REG.block("utb1_flex", new PipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::pipeSupplier, false), PipeBlockItem::new);
	public static final PipeBlock UTB1_PIPE_GLOW = REG.block("utb1_flex_g", new PipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::pipeSupplier, true), PipeBlockItem::new);
	public static final PipeBlock UTB1_STRAIGHT_PIPE = REG.block("utb1_straight", new StraightPipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::pipeSupplier, false), PipeBlockItem::new);
	public static final PipeBlock UTB1_STRAIGHT_PIPE_GLOW = REG.block("utb1_straight_g", new StraightPipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::pipeSupplier, true), PipeBlockItem::new);
	public static final BlockEntityType<PipeBlockEntity> UTB1_BLOCK_ENTITY_TYPE = REG.blockEntityType("utb1", PipeBlocks::pipeSupplier, UTB1_PIPE, UTB1_STRAIGHT_PIPE, UTB1_PIPE_GLOW, UTB1_STRAIGHT_PIPE_GLOW);
	static PipeBlockEntity pipeSupplier(BlockPos pos, BlockState state) {
		return new PipeBlockEntity(UTB1_BLOCK_ENTITY_TYPE, pos, state);
	}


	public static final ItemMoverBlock UTB1_S2B = REG.block("utb1_intake", new ItemMoverBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::intakeSupplier, false), PipeBlockItem::new);
	public static final ItemMoverBlock UTB1_S2B_GLOW = REG.block("utb1_intake_g", new ItemMoverBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::intakeSupplier, true), PipeBlockItem::new);
	public static final BlockEntityType<StorageToBusBlockEntity> UTB1_S2B_BLOCK_ENTITY_TYPE = REG.blockEntityType("utb1_intake", PipeBlocks::intakeSupplier, UTB1_S2B, UTB1_S2B_GLOW);
	static StorageToBusBlockEntity intakeSupplier(BlockPos pos, BlockState state) {
		return new StorageToBusBlockEntity(UTB1_S2B_BLOCK_ENTITY_TYPE, pos, state);
	}

	public static final ItemMoverBlock UTB1_B2S = REG.block("utb1_export", new ItemMoverBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::exportSupplier, false), PipeBlockItem::new);
	public static final ItemMoverBlock UTB1_B2S_GLOW = REG.block("utb1_export_g", new ItemMoverBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::exportSupplier, true), PipeBlockItem::new);
	public static final BlockEntityType<BusToStorageBlockEntity> UTB1_B2S_BLOCK_ENTITY_TYPE = REG.blockEntityType("utb1_export", PipeBlocks::exportSupplier, UTB1_B2S, UTB1_B2S_GLOW);
	static BusToStorageBlockEntity exportSupplier(BlockPos pos, BlockState state) {
		return new BusToStorageBlockEntity(UTB1_B2S_BLOCK_ENTITY_TYPE, pos, state);
	}

	static {
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
