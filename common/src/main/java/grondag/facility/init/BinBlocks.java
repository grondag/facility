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

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.facility.Facility;
import grondag.facility.storage.item.BinBlock;
import grondag.facility.storage.item.BinBlockEntity;
import grondag.facility.storage.item.CrateBlock;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.facility.storage.item.CreativeBinStorage;
import grondag.facility.storage.item.PortableCrateItem;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.discrete.DividedDiscreteStore;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.item.XmItemRegistry;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.CubeWithFace;

@SuppressWarnings("unchecked")
public abstract class BinBlocks {
	private BinBlocks() { }

	private static BinBlock BIN_X1;
	private static BlockEntityType<BinBlockEntity> BIN_X1_BLOCK_ENTITY_TYPE;

	public static BlockEntityType<BinBlockEntity> BIN_X1_BLOCK_ENTITY_TYPE() {
		return BIN_X1_BLOCK_ENTITY_TYPE;
	}

	private static BinBlockEntity binX1Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(BIN_X1_BLOCK_ENTITY_TYPE, pos, state, () -> new DividedDiscreteStore(1, 2048).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "BIN 1x ", 1);
	}

	private static PortableCrateItem PORTABLE_BIN_ITEM_X1;
	private static Item BIN_ITEM_X1;

	private static BinBlock BIN_X2;
	private static BlockEntityType<BinBlockEntity> BIN_X2_BLOCK_ENTITY_TYPE;

	public static BlockEntityType<BinBlockEntity> BIN_X2_BLOCK_ENTITY_TYPE() {
		return BIN_X2_BLOCK_ENTITY_TYPE;
	}

	private static BinBlockEntity binX2Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(BIN_X2_BLOCK_ENTITY_TYPE, pos, state, () -> new DividedDiscreteStore(2, 1024).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "BIN 2x ", 2);
	}

	private static PortableCrateItem PORTABLE_BIN_ITEM_X2;
	private static Item BIN_ITEM_X2;

	private static BinBlock BIN_X4;
	private static BlockEntityType<BinBlockEntity> BIN_X4_BLOCK_ENTITY_TYPE;

	public static BlockEntityType<BinBlockEntity> BIN_X4_BLOCK_ENTITY_TYPE() {
		return BIN_X4_BLOCK_ENTITY_TYPE;
	}

	private static BinBlockEntity binX4Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(BIN_X4_BLOCK_ENTITY_TYPE, pos, state, () -> new DividedDiscreteStore(4, 512).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "BIN 4x ", 4);
	}

	private static PortableCrateItem PORTABLE_BIN_ITEM_X4;
	private static Item BIN_ITEM_X4;

	private static BinBlock CREATIVE_BIN_X1;
	private static BlockEntityType<BinBlockEntity> CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE;

	public static BlockEntityType<BinBlockEntity> CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE() {
		return CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE;
	}

	private static BinBlockEntity creativeBinX1Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE, pos, state, () -> new CreativeBinStorage(1, 2048).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "CREATIVE BIN 1x ", 1);
	}

	private static BinBlock CREATIVE_BIN_X2;
	private static BlockEntityType<BinBlockEntity> CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE;

	public static BlockEntityType<BinBlockEntity> CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE() {
		return CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE;
	}

	private static BinBlockEntity creativeBinX2Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE, pos, state, () -> new CreativeBinStorage(2, 1024).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "CREATIVE BIN 2x ", 2);
	}

	private static BinBlock CREATIVE_BIN_X4;
	private static BlockEntityType<BinBlockEntity> CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE;

	public static BlockEntityType<BinBlockEntity> CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE() {
		return CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE;
	}

	private static BinBlockEntity creativeBinX4Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE, pos, state, () -> new CreativeBinStorage(4, 512).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "CREATIVE BIN 4x ", 4);
	}

	public static void initialize() {
		BIN_X1 = Facility.blockNoItem("bin_1x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::binX1Be, 1, false));
		BIN_X1_BLOCK_ENTITY_TYPE = Facility.blockEntityType("bin_x1", BinBlocks::binX1Be, BIN_X1);
		PORTABLE_BIN_ITEM_X1 = Facility.item("bin_1x_item", new PortableCrateItem(BIN_X1, Facility.itemSettings().stacksTo(1).durability(2048), () -> new DividedDiscreteStore(1, 2048).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		BIN_ITEM_X1 = Facility.item("bin_1x", new BlockItem(BIN_X1, Facility.itemSettings()));

		BIN_X2 = Facility.blockNoItem("bin_2x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::binX2Be, 2, false));
		BIN_X2_BLOCK_ENTITY_TYPE = Facility.blockEntityType("bin_x2", BinBlocks::binX2Be, BIN_X2);
		PORTABLE_BIN_ITEM_X2 = Facility.item("bin_2x_item", new PortableCrateItem(BIN_X2, Facility.itemSettings().stacksTo(1).durability(2048), () -> new DividedDiscreteStore(2, 1024).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		BIN_ITEM_X2 = Facility.item("bin_2x", new BlockItem(BIN_X2, Facility.itemSettings()));

		BIN_X4 = Facility.blockNoItem("bin_4x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::binX4Be, 4, false));
		BIN_X4_BLOCK_ENTITY_TYPE = Facility.blockEntityType("bin_x4", BinBlocks::binX4Be, BIN_X4);
		PORTABLE_BIN_ITEM_X4 = Facility.item("bin_4x_item", new PortableCrateItem(BIN_X4, Facility.itemSettings().stacksTo(1).durability(2048), () -> new DividedDiscreteStore(4, 512).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		BIN_ITEM_X4 = Facility.item("bin_4x", new BlockItem(BIN_X4, Facility.itemSettings()));

		CREATIVE_BIN_X1 = Facility.block("creative_bin_1x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::creativeBinX1Be, 1, true));
		CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE = Facility.blockEntityType("creative_bin_x1", BinBlocks::creativeBinX1Be, CREATIVE_BIN_X1);
		CREATIVE_BIN_X2 = Facility.block("creative_bin_2x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::creativeBinX2Be, 2, true));
		CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE = Facility.blockEntityType("creative_bin_x2", BinBlocks::creativeBinX2Be, CREATIVE_BIN_X2);

		CREATIVE_BIN_X4 = Facility.block("creative_bin_4x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::creativeBinX4Be, 4, true));
		CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE = Facility.blockEntityType("creative_bin_x4", BinBlocks::creativeBinX4Be, CREATIVE_BIN_X4);

		BIN_X1.portableItem = PORTABLE_BIN_ITEM_X1;
		BIN_X2.portableItem = PORTABLE_BIN_ITEM_X2;
		BIN_X4.portableItem = PORTABLE_BIN_ITEM_X4;

		//CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);
		Store.STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage(), BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getInternalStorage(), BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage().getConsumer(), BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage().getSupplier(), BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);

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

		XmItemRegistry.addItem(PORTABLE_BIN_ITEM_X1, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION);
		XmItemRegistry.addItem(PORTABLE_BIN_ITEM_X2, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION);
		XmItemRegistry.addItem(PORTABLE_BIN_ITEM_X4, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION);
	}
}
