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
import grondag.facility.transport.PipeModel;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;

@SuppressWarnings("unchecked")
public enum PipeBlocks {
	;

	public static final PipeBlock PIPE = REG.block("basic_pipe", new PipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1).build(), PipeBlocks::pipeSupplier));
	public static final BlockEntityType<PipeBlockEntity> PIPE_BLOCK_ENTITY_TYPE = REG.blockEntityType("basic_pipe", PipeBlocks::pipeSupplier, PIPE);
	static PipeBlockEntity pipeSupplier() {
		return new PipeBlockEntity(PIPE_BLOCK_ENTITY_TYPE);
	}

	static {
		CarrierProvider.CARRIER_PROVIDER_COMPONENT.addProvider(ctx -> ((PipeBlockEntity) ctx.blockEntity()).carrierProvider, PIPE);

		XmBlockRegistry.addBlockStates(PIPE, bs -> PrimitiveStateFunction.builder()
				.withJoin(PipeBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(PipeModel.MODEL_STATE_UPDATE)
				.withDefaultState((SpeciesProperty.SPECIES_MODIFIER.mutate(
						PipeModel.PRIMITIVE.newState()
						.paint(PipeModel.SURFACE_SIDE, PipeModel.PAINT_SIDE)
						.paint(PipeModel.SURFACE_END, PipeModel.PAINT_END)
						.paint(PipeModel.SURFACE_CONNECTOR, PipeModel.PAINT_CONNECTOR), bs)))
				.build());
	}
}
