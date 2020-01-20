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

import grondag.facility.storage.item.BinBlock;
import grondag.facility.storage.item.BinBlockEntity;
import grondag.facility.storage.item.CrateBlock;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.facility.storage.item.CreativeBinStorage;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.discrete.DividedDiscreteStore;
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

	public static final BinBlock BIN_X1 = REG.block("bin_1x", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX1Be, 1, false));
	public static final BlockEntityType<BinBlockEntity> BIN_X1_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x1", BinBlocks::binX1Be, BIN_X1);
	private static BinBlockEntity binX1Be() {
		return new BinBlockEntity(BIN_X1_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStore(1, 2048).filter(CrateBlocks.FILTER_NESTING), "BIN 1x ", 1);
	}

	public static final BinBlock BIN_X2 = REG.block("bin_2x", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX2Be, 2, false));
	public static final BlockEntityType<BinBlockEntity> BIN_X2_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x2", BinBlocks::binX2Be, BIN_X2);
	private static BinBlockEntity binX2Be() {
		return new BinBlockEntity(BIN_X2_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStore(2, 1024).filter(CrateBlocks.FILTER_NESTING), "BIN 2x ", 2);
	}

	public static final BinBlock BIN_X4 = REG.block("bin_4x", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::binX4Be, 4, false));
	public static final BlockEntityType<BinBlockEntity> BIN_X4_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x4", BinBlocks::binX4Be, BIN_X4);
	private static BinBlockEntity binX4Be() {
		return new BinBlockEntity(BIN_X4_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStore(4, 512).filter(CrateBlocks.FILTER_NESTING), "BIN 4x ", 4);
	}

	public static final BinBlock CREATIVE_BIN_X1 = REG.block("creative_bin_1x", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::creativeBinX1Be, 1, true));
	public static final BlockEntityType<BinBlockEntity> CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE = REG.blockEntityType("creative_bin_x1", BinBlocks::creativeBinX1Be, CREATIVE_BIN_X1);
	private static BinBlockEntity creativeBinX1Be() {
		return new BinBlockEntity(CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE, () -> new CreativeBinStorage(1, 2048).filter(CrateBlocks.FILTER_NESTING), "CREATIVE BIN 1x ", 1);
	}

	public static final BinBlock CREATIVE_BIN_X2 = REG.block("creative_bin_2x", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::creativeBinX2Be, 2, true));
	public static final BlockEntityType<BinBlockEntity> CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE = REG.blockEntityType("creative_bin_x2", BinBlocks::creativeBinX2Be, CREATIVE_BIN_X2);
	private static BinBlockEntity creativeBinX2Be() {
		return new BinBlockEntity(CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE, () -> new CreativeBinStorage(2, 1024).filter(CrateBlocks.FILTER_NESTING), "CREATIVE BIN 2x ", 2);
	}

	public static final BinBlock CREATIVE_BIN_X4 = REG.block("creative_bin_4x", new BinBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), BinBlocks::creativeBinX4Be, 4, true));
	public static final BlockEntityType<BinBlockEntity> CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE = REG.blockEntityType("creative_bin_x4", BinBlocks::creativeBinX4Be, CREATIVE_BIN_X4);
	private static BinBlockEntity creativeBinX4Be() {
		return new BinBlockEntity(CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE, () -> new CreativeBinStorage(4, 512).filter(CrateBlocks.FILTER_NESTING), "CREATIVE BIN 4x ", 4);
	}

	static {
		CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);
		Store.STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage(), BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getInternalStorage(), BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);

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


		final XmPaint creativePaint = Textures.crateBaseFinder(2).textureColor(0, 0xFF00FFFF).find();

		XmBlockRegistry.addBlockStates(BinBlocks.CREATIVE_BIN_X1, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(creativePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.BIN_FACE, 0xFFE0FFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(CREATIVE_BIN_X2, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(creativePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.HALF_DIVIDER, 0xFFE0FFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(CREATIVE_BIN_X4, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(creativePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.QUARTER_DIVIDER, 0xFFE0FFFF)), bs), bs))
				.build());

	}
}
