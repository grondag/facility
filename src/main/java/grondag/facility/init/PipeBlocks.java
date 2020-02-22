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

import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

import grondag.facility.transport.PipeBlock;
import grondag.facility.transport.PipeBlockEntity;
import grondag.facility.transport.StraightPipeBlock;
import grondag.facility.transport.item.IntakeBlock;
import grondag.facility.transport.item.IntakeBlockEntity;
import grondag.facility.transport.model.ExporterModel;
import grondag.facility.transport.model.PipeModel;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;

@SuppressWarnings("unchecked")
public enum PipeBlocks {
	;

	public static final PipeBlock UTB1_PIPE = REG.block("utb1_flex", new PipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1).build(), PipeBlocks::pipeSupplier));
	public static final PipeBlock UTB1_STRAIGHT_PIPE = REG.block("utb1_straight", new StraightPipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1).build(), PipeBlocks::pipeSupplier));
	public static final BlockEntityType<PipeBlockEntity> UTB1_BLOCK_ENTITY_TYPE = REG.blockEntityType("utb1", PipeBlocks::pipeSupplier, UTB1_PIPE, UTB1_STRAIGHT_PIPE);
	static PipeBlockEntity pipeSupplier() {
		return new PipeBlockEntity(UTB1_BLOCK_ENTITY_TYPE);
	}


	public static final IntakeBlock UTB1_INTAKE = REG.block("utb1_intake", new IntakeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1).build(), PipeBlocks::intakeSupplier));
	public static final BlockEntityType<IntakeBlockEntity> UTB1_INTAKE_BLOCK_ENTITY_TYPE = REG.blockEntityType("utb1_intake", PipeBlocks::intakeSupplier, UTB1_INTAKE);
	static IntakeBlockEntity intakeSupplier() {
		return new IntakeBlockEntity(UTB1_INTAKE_BLOCK_ENTITY_TYPE);
	}

	static {
		CarrierProvider.CARRIER_PROVIDER_COMPONENT.registerProvider(ctx -> ((PipeBlockEntity) ctx.blockEntity()).getCarrierProvider(ctx), UTB1_PIPE, UTB1_STRAIGHT_PIPE, UTB1_INTAKE);

		XmBlockRegistry.addBlockStates(UTB1_PIPE, bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModel.MODEL_STATE_UPDATE)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						PipeModel.PRIMITIVE.newState()
						.primitiveBits(0b111111)
						.paint(PipeModel.SURFACE_SIDE, PipeModel.PAINT_SIDE)
						.paint(PipeModel.SURFACE_END, PipeModel.PAINT_END)
						.paint(PipeModel.SURFACE_CONNECTOR, PipeModel.PAINT_CONNECTOR)
						.simpleJoin(SimpleJoinState.ALL_JOINS), bs)))

				.build());

		XmBlockRegistry.addBlockStates(UTB1_STRAIGHT_PIPE, bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST_WITH_AXIS)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModel.MODEL_STATE_UPDATE)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						PipeModel.PRIMITIVE.newState()
						.primitiveBits(0b11)
						.paint(PipeModel.SURFACE_SIDE, PipeModel.PAINT_SIDE)
						.paint(PipeModel.SURFACE_END, PipeModel.PAINT_END)
						.paint(PipeModel.SURFACE_CONNECTOR, PipeModel.PAINT_CONNECTOR)
						.simpleJoin(SimpleJoinState.ALL_JOINS), bs)))
				.build());

		XmBlockRegistry.addBlockStates(UTB1_INTAKE, bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModel.MODEL_STATE_UPDATE)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						ExporterModel.PRIMITIVE.newState()
						.paint(PipeModel.SURFACE_SIDE, PipeModel.PAINT_SIDE)
						.paint(PipeModel.SURFACE_END, PipeModel.PAINT_END)
						.paint(PipeModel.SURFACE_CONNECTOR, PipeModel.PAINT_CONNECTOR)
						.simpleJoin(SimpleJoinState.NO_JOINS), bs)))
				.build());
	}
}
