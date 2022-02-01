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

	private static BlockEntityType<BinBlockEntity> binBlockEntityTypeX1;

	public static BlockEntityType<BinBlockEntity> binBlockEntityTypeX1() {
		return binBlockEntityTypeX1;
	}

	private static BinBlockEntity binX1Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(binBlockEntityTypeX1, pos, state, () -> new DividedDiscreteStore(1, 2048).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "BIN 1x ", 1);
	}

	private static BlockEntityType<BinBlockEntity> binBlockEntityTypeX2;

	public static BlockEntityType<BinBlockEntity> binBlockEntityTypeX2() {
		return binBlockEntityTypeX2;
	}

	private static BinBlockEntity binX2Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(binBlockEntityTypeX2, pos, state, () -> new DividedDiscreteStore(2, 1024).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "BIN 2x ", 2);
	}

	private static BlockEntityType<BinBlockEntity> binBlockEntityTypeX4;

	public static BlockEntityType<BinBlockEntity> binBlockEntityTypeX4() {
		return binBlockEntityTypeX4;
	}

	private static BinBlockEntity binX4Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(binBlockEntityTypeX4, pos, state, () -> new DividedDiscreteStore(4, 512).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "BIN 4x ", 4);
	}

	private static BlockEntityType<BinBlockEntity> creativeBinBlockEntityTypeX1;

	public static BlockEntityType<BinBlockEntity> creativeBinBlockEntityTypeX1() {
		return creativeBinBlockEntityTypeX1;
	}

	private static BinBlockEntity creativeBinX1Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(creativeBinBlockEntityTypeX1, pos, state, () -> new CreativeBinStorage(1, 2048).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "CREATIVE BIN 1x ", 1);
	}

	private static BlockEntityType<BinBlockEntity> creativeBinBlockEntityTypeX2;

	public static BlockEntityType<BinBlockEntity> creativeBinBlockEntityTypeX2() {
		return creativeBinBlockEntityTypeX2;
	}

	private static BinBlockEntity creativeBinX2Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(creativeBinBlockEntityTypeX2, pos, state, () -> new CreativeBinStorage(2, 1024).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "CREATIVE BIN 2x ", 2);
	}

	private static BlockEntityType<BinBlockEntity> creativeBinBlockEntityTypeX4;

	public static BlockEntityType<BinBlockEntity> creativeBinBlockEntityTypeX4() {
		return creativeBinBlockEntityTypeX4;
	}

	private static BinBlockEntity creativeBinX4Be(BlockPos pos, BlockState state) {
		return new BinBlockEntity(creativeBinBlockEntityTypeX4, pos, state, () -> new CreativeBinStorage(4, 512).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "CREATIVE BIN 4x ", 4);
	}

	public static void initialize() {
		final var binX1 = Facility.blockNoItem("bin_1x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::binX1Be, 1, false));
		binBlockEntityTypeX1 = Facility.blockEntityType("bin_x1", BinBlocks::binX1Be, binX1);
		final var portableBinItemX1 = Facility.item("bin_1x_item", new PortableCrateItem(binX1, Facility.itemSettings().stacksTo(1).durability(2048), () -> new DividedDiscreteStore(1, 2048).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		Facility.blockItem("bin_1x", new BlockItem(binX1, Facility.itemSettings()));
		binX1.portableItem = portableBinItemX1;

		final var binX2 = Facility.blockNoItem("bin_2x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::binX2Be, 2, false));
		binBlockEntityTypeX2 = Facility.blockEntityType("bin_x2", BinBlocks::binX2Be, binX2);
		final var portableBinItemX2 = Facility.item("bin_2x_item", new PortableCrateItem(binX2, Facility.itemSettings().stacksTo(1).durability(2048), () -> new DividedDiscreteStore(2, 1024).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		Facility.blockItem("bin_2x", new BlockItem(binX2, Facility.itemSettings()));
		binX2.portableItem = portableBinItemX2;

		final var binX4 = Facility.blockNoItem("bin_4x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::binX4Be, 4, false));
		binBlockEntityTypeX4 = Facility.blockEntityType("bin_x4", BinBlocks::binX4Be, binX4);
		final var portableBinItemX4 = Facility.item("bin_4x_item", new PortableCrateItem(binX4, Facility.itemSettings().stacksTo(1).durability(2048), () -> new DividedDiscreteStore(4, 512).filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		binX4.portableItem = portableBinItemX4;
		Facility.blockItem("bin_4x", new BlockItem(binX4, Facility.itemSettings()));

		final var creativeBinX1 = Facility.block("creative_bin_1x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::creativeBinX1Be, 1, true));
		creativeBinBlockEntityTypeX1 = Facility.blockEntityType("creative_bin_x1", BinBlocks::creativeBinX1Be, creativeBinX1);
		final var creativeBinX2 = Facility.block("creative_bin_2x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::creativeBinX2Be, 2, true));
		creativeBinBlockEntityTypeX2 = Facility.blockEntityType("creative_bin_x2", BinBlocks::creativeBinX2Be, creativeBinX2);
		final var creativeBinX4 = Facility.block("creative_bin_4x", new BinBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), BinBlocks::creativeBinX4Be, 4, true));
		creativeBinBlockEntityTypeX4 = Facility.blockEntityType("creative_bin_x4", BinBlocks::creativeBinX4Be, creativeBinX4);

		//CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(BIN_X1, BIN_X2, BIN_X4, CREATIVE_BIN_X1, CREATIVE_BIN_X2, CREATIVE_BIN_X4);
		Store.STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage(), binX1, binX2, binX4, creativeBinX1, creativeBinX2, creativeBinX4);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getInternalStorage(), binX1, binX2, binX4, creativeBinX1, creativeBinX2, creativeBinX4);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage().getConsumer(), binX1, binX2, binX4, creativeBinX1, creativeBinX2, creativeBinX4);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage().getSupplier(), binX1, binX2, binX4, creativeBinX1, creativeBinX2, creativeBinX4);

		final XmPaint basePaint = Textures.crateBaseFinder(2).find();

		XmBlockRegistry.addBlockStates(binX1, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.BIN_FACE, 0xFFFFFFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(binX2, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.HALF_DIVIDER, 0xFFFFFFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(binX4, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.QUARTER_DIVIDER, 0xFFFFFFFF)), bs), bs))
				.build());

		final XmPaint creativePaint = Textures.crateBaseFinder(2).textureColor(0, 0xFF00FFFF).find();

		XmBlockRegistry.addBlockStates(creativeBinX1, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(creativePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.BIN_FACE, 0xFFE0FFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(creativeBinX2, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(creativePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.HALF_DIVIDER, 0xFFE0FFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(creativeBinX4, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(creativePaint)
						.paint(CubeWithFace.SURFACE_TOP, Textures.cratePaintWithDecal(Textures.QUARTER_DIVIDER, 0xFFE0FFFF)), bs), bs))
				.build());

		XmItemRegistry.addItem(portableBinItemX1, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION_V2);
		XmItemRegistry.addItem(portableBinItemX2, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION_V2);
		XmItemRegistry.addItem(portableBinItemX4, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION_V2);
	}
}
