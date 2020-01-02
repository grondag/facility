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

import grondag.facility.storage.BinBlock;
import grondag.facility.storage.BinBlockEntity;
import grondag.facility.storage.CrateBlock;
import grondag.facility.storage.CrateBlockEntity;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.storage.discrete.DividedDiscreteStorage;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.CubeWithFace;

@SuppressWarnings("unchecked")
public enum BinBlocks {
	;

	public static final BinBlock BIN_X1 = REG.block("bin_x1", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX1Be, 1, false));
	public static final BlockEntityType<BinBlockEntity> BIN_X1_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x1", BinBlocks::binX1Be, BIN_X1);

	static BinBlockEntity binX1Be() {
		return new BinBlockEntity(BIN_X1_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStorage(1, 2048).filter(CrateBlocks.FILTER_NESTING), "BINx1 ", 1);
	}


	public static final BinBlock BIN_X2 = REG.block("bin_x2", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX2Be, 2, false));
	public static final BlockEntityType<BinBlockEntity> BIN_X2_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x2", BinBlocks::binX2Be, BIN_X2);

	static BinBlockEntity binX2Be() {
		return new BinBlockEntity(BIN_X2_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStorage(2, 1024).filter(CrateBlocks.FILTER_NESTING), "BINx2 ", 2);
	}


	public static final BinBlock BIN_X4 = REG.block("bin_x4", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX4Be, 4, false));
	public static final BlockEntityType<BinBlockEntity> BIN_X4_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x4", BinBlocks::binX4Be, BIN_X4);

	static BinBlockEntity binX4Be() {
		return new BinBlockEntity(BIN_X4_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStorage(4, 512).filter(CrateBlocks.FILTER_NESTING), "BINx4 ", 4);
	}


	public static final BinBlock CREATIVE_BIN_X1 = REG.block("creative_bin_x1", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX1Be, 1, true));
	public static final BinBlock CREATIVE_BIN_X2 = REG.block("creative_bin_x2", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX2Be, 2, true));
	public static final BinBlock CREATIVE_BIN_X4 = REG.block("creative_bin_x4", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX4Be, 4, true));


	static {
		CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(BIN_X1, BIN_X2, BIN_X4);
		Storage.STORAGE_COMPONENT.addProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage(), BIN_X1, BIN_X2, BIN_X4);
		Storage.INTERNAL_STORAGE_COMPONENT.addProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getInternalStorage(), BIN_X1, BIN_X2, BIN_X4);

		// TODO: for creative
		//Storage.STORAGE_COMPONENT.addProvider(ctx -> Storage.CREATIVE, ITEM_SUPPLIER);
		//Storage.INTERNAL_STORAGE_COMPONENT.addProvider(ctx -> Storage.CREATIVE, ITEM_SUPPLIER);

		final XmPaint basePaint = Textures.crateBaseFinder(2).find();

		XmBlockRegistry.addBlockStates(BinBlocks.BIN_X1, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.BIN_FACE, 0xFFFFFFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(BIN_X2, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.HALF_DIVIDER, 0xFFFFFFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(BIN_X4, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.QUARTER_DIVIDER, 0xFFFFFFFF)), bs), bs))
				.build());
	}
}
