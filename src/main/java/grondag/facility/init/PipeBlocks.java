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
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import grondag.facility.transport.PipeBlock;
import grondag.facility.transport.PipeBlockEntity;
import grondag.facility.transport.StraightPipeBlock;
import grondag.facility.transport.item.ExportBlockEntity;
import grondag.facility.transport.item.IntakeBlockEntity;
import grondag.facility.transport.item.ItemMoverBlock;
import grondag.facility.transport.model.ItemMoverModel;
import grondag.facility.transport.model.PipeModel;
import grondag.facility.transport.model.PipeModifiers;
import grondag.facility.transport.model.PipePaints;
import grondag.fermion.orientation.api.DirectionHelper;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;

@SuppressWarnings("unchecked")
public enum PipeBlocks {
	;

	public static final PipeBlock UTB1_PIPE = REG.block("utb1_flex", new PipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::pipeSupplier, false));
	public static final PipeBlock UTB1_STRAIGHT_PIPE = REG.block("utb1_straight", new StraightPipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::pipeSupplier, false));
	public static final PipeBlock UTB1_PIPE_GLOW = REG.block("utb1_flex_g", new PipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::pipeSupplier, true));
	public static final PipeBlock UTB1_STRAIGHT_PIPE_GLOW = REG.block("utb1_straight_g", new StraightPipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::pipeSupplier, true));
	public static final BlockEntityType<PipeBlockEntity> UTB1_BLOCK_ENTITY_TYPE = REG.blockEntityType("utb1", PipeBlocks::pipeSupplier, UTB1_PIPE, UTB1_STRAIGHT_PIPE, UTB1_PIPE_GLOW, UTB1_STRAIGHT_PIPE_GLOW);
	static PipeBlockEntity pipeSupplier() {
		return new PipeBlockEntity(UTB1_BLOCK_ENTITY_TYPE);
	}


	public static final ItemMoverBlock UTB1_INTAKE = REG.block("utb1_intake", new ItemMoverBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::intakeSupplier, false));
	public static final ItemMoverBlock UTB1_INTAKE_GLOW = REG.block("utb1_intake_g", new ItemMoverBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::intakeSupplier, true));
	public static final BlockEntityType<IntakeBlockEntity> UTB1_INTAKE_BLOCK_ENTITY_TYPE = REG.blockEntityType("utb1_intake", PipeBlocks::intakeSupplier, UTB1_INTAKE, UTB1_INTAKE_GLOW);
	static IntakeBlockEntity intakeSupplier() {
		return new IntakeBlockEntity(UTB1_INTAKE_BLOCK_ENTITY_TYPE);
	}

	public static final ItemMoverBlock UTB1_EXPORT = REG.block("utb1_export", new ItemMoverBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::exportSupplier, false));
	public static final ItemMoverBlock UTB1_EXPORT_GLOW = REG.block("utb1_export_g", new ItemMoverBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1), PipeBlocks::exportSupplier, true));
	public static final BlockEntityType<ExportBlockEntity> UTB1_EXPORT_BLOCK_ENTITY_TYPE = REG.blockEntityType("utb1_export", PipeBlocks::exportSupplier, UTB1_EXPORT, UTB1_EXPORT_GLOW);
	static ExportBlockEntity exportSupplier() {
		return new ExportBlockEntity(UTB1_EXPORT_BLOCK_ENTITY_TYPE);
	}

	static {
		CarrierProvider.CARRIER_PROVIDER_COMPONENT.registerProvider(ctx -> ((PipeBlockEntity) ctx.blockEntity()).getCarrierProvider(ctx), UTB1_PIPE, UTB1_STRAIGHT_PIPE, UTB1_INTAKE, UTB1_EXPORT);

		final Function<BlockState, PrimitiveStateFunction> UTB1_FUNC = bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModifiers.OMNI_PIPE_UPDATE)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						PipeModel.PRIMITIVE.newState()
						.alternateJoinBits(0b111111)
						.paint(PipeModel.SURFACE_CABLE, PipePaints.CABLE)
						.paint(PipeModel.SURFACE_CONNECTOR_FACE, PipePaints.STD_CONNECTOR_FACE)
						.paint(PipeModel.SURFACE_CONNECTOR_SIDE, PipePaints.STD_CONNECTOR_SIDE)
						.paint(PipeModel.SURFACE_CONNECTOR_BACK, PipePaints.STD_CONNECTOR_BACK)
						.simpleJoin(SimpleJoinState.ALL_JOINS), bs)))
				.build();

		XmBlockRegistry.addBlockStates(UTB1_PIPE, UTB1_FUNC);
		XmBlockRegistry.addBlockStates(UTB1_PIPE_GLOW, UTB1_FUNC);

		final Function<BlockState, PrimitiveStateFunction> UTB1_STRAIGHT_FUNC = bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST_WITH_AXIS)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModifiers.AXIS_PIPE_UPDATE)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						PipeModel.PRIMITIVE.newState()
						.alternateJoinBits(DirectionHelper.NORTH_BIT | DirectionHelper.SOUTH_BIT)
						.paint(PipeModel.SURFACE_CABLE, PipePaints.CABLE)
						.paint(PipeModel.SURFACE_CONNECTOR_FACE, PipePaints.STD_CONNECTOR_FACE)
						.paint(PipeModel.SURFACE_CONNECTOR_BACK, PipePaints.STD_CONNECTOR_BACK)
						.paint(PipeModel.SURFACE_CONNECTOR_SIDE, PipePaints.STD_CONNECTOR_SIDE)
						.simpleJoin(SimpleJoinState.Z_JOINS), bs)))
				.build();

		XmBlockRegistry.addBlockStates(UTB1_STRAIGHT_PIPE, UTB1_STRAIGHT_FUNC);
		XmBlockRegistry.addBlockStates(UTB1_STRAIGHT_PIPE_GLOW, UTB1_STRAIGHT_FUNC);

		final Function<BlockState, PrimitiveStateFunction> UTB1_INTAKE_FUNC = bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModifiers.MOVER_PIPE_UPDATE)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						ItemMoverModel.PRIMITIVE.newState()
						.alternateJoinBits(DirectionHelper.DOWN_BIT | DirectionHelper.UP_BIT)
						.paint(PipeModel.SURFACE_CABLE, PipePaints.CABLE)
						.paint(PipeModel.SURFACE_CONNECTOR_FACE, PipePaints.STD_CONNECTOR_FACE)
						.paint(PipeModel.SURFACE_CONNECTOR_BACK, PipePaints.STD_CONNECTOR_BACK)
						.paint(PipeModel.SURFACE_CONNECTOR_SIDE, PipePaints.STD_CONNECTOR_SIDE)
						.paint(ItemMoverModel.SURFACE_MOVER_FACE, PipePaints.INPUT_CONNECTOR_FACE)
						.paint(ItemMoverModel.SURFACE_MOVER_BACK, PipePaints.INPUT_CONNECTOR_BACK)
						.paint(ItemMoverModel.SURFACE_MOVER_SIDE, PipePaints.INPUT_CONNECTOR_SIDE)
						.orientationIndex(Direction.UP.ordinal())
						.simpleJoin(SimpleJoinState.Y_JOINS), bs)))
				.build();

		XmBlockRegistry.addBlockStates(UTB1_INTAKE, UTB1_INTAKE_FUNC);
		XmBlockRegistry.addBlockStates(UTB1_INTAKE_GLOW, UTB1_INTAKE_FUNC);


		final Function<BlockState, PrimitiveStateFunction> UTB1_EXPORT_FUNC = bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModifiers.MOVER_PIPE_UPDATE)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						ItemMoverModel.PRIMITIVE.newState()
						.alternateJoinBits(DirectionHelper.DOWN_BIT | DirectionHelper.UP_BIT)
						.paint(PipeModel.SURFACE_CABLE, PipePaints.CABLE)
						.paint(PipeModel.SURFACE_CONNECTOR_FACE, PipePaints.STD_CONNECTOR_FACE)
						.paint(PipeModel.SURFACE_CONNECTOR_BACK, PipePaints.STD_CONNECTOR_BACK)
						.paint(PipeModel.SURFACE_CONNECTOR_SIDE, PipePaints.STD_CONNECTOR_SIDE)
						.paint(ItemMoverModel.SURFACE_MOVER_FACE, PipePaints.OUTPUT_CONNECTOR_FACE)
						.paint(ItemMoverModel.SURFACE_MOVER_BACK, PipePaints.OUTPUT_CONNECTOR_BACK)
						.paint(ItemMoverModel.SURFACE_MOVER_SIDE, PipePaints.OUTPUT_CONNECTOR_SIDE)
						.orientationIndex(Direction.DOWN.ordinal())
						.simpleJoin(SimpleJoinState.Y_JOINS), bs)))
				.build();

		XmBlockRegistry.addBlockStates(UTB1_EXPORT, UTB1_EXPORT_FUNC);
		XmBlockRegistry.addBlockStates(UTB1_EXPORT_GLOW, UTB1_EXPORT_FUNC);
	}
}
