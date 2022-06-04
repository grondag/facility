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

import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import grondag.facility.Facility;
import grondag.facility.storage.item.CrateBlock;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.facility.storage.item.CreativeCrateBlock;
import grondag.facility.storage.item.CreativeCrateBlockEntity;
import grondag.facility.storage.item.PortableCrateItem;
import grondag.facility.storage.item.SlottedCrateBlockEntity;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.discrete.FlexibleDiscreteStore;
import grondag.fluidity.base.storage.discrete.SlottedInventoryStore;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.item.XmItemRegistry;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.Cube;
import grondag.xm.api.primitive.simple.CubeWithFace;

@SuppressWarnings("unchecked")
public abstract class CrateBlocks {
	private CrateBlocks() { }

	public static final Predicate<Article> FILTER_TYPE_AND_NESTING = d -> {
		if (d.type() != ArticleType.ITEM) return false;

		final Item item = d.toItem();

		if (d.hasTag() && item.builtInRegistryHolder().is(Facility.STORAGE_BLACKLIST_WITH_CONTENT)) return false;

		if (item.builtInRegistryHolder().is(Facility.STORAGE_BLACKLIST_ALWAYS)) return false;

		return true;
	};

	private static BlockEntityType<CrateBlockEntity> crateBlockEntityType;

	public static BlockEntityType<CrateBlockEntity> crateBlockEntityType() {
		return crateBlockEntityType;
	}

	private static CrateBlockEntity crateBe(BlockPos pos, BlockState state) {
		return new CrateBlockEntity(crateBlockEntityType, pos, state, () -> new FlexibleDiscreteStore(2048).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "CRATE ");
	}

	private static BlockEntityType<SlottedCrateBlockEntity> slottedCrateBlockEntityType;

	public static BlockEntityType<SlottedCrateBlockEntity> slottedCrateBlockEntityType() {
		return slottedCrateBlockEntityType;
	}

	private static SlottedCrateBlockEntity slottedBe(BlockPos pos, BlockState state) {
		return new SlottedCrateBlockEntity(slottedCrateBlockEntityType, pos, state, () -> new SlottedInventoryStore(32).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "SLOTTED CRATE ");
	}

	private static BlockEntityType<CreativeCrateBlockEntity> creativeCrateBlockEntityType;

	private static CreativeCrateBlockEntity itemSupplier(BlockPos pos, BlockState state) {
		return new CreativeCrateBlockEntity(creativeCrateBlockEntityType, pos, state, true);
	}

	private static BlockEntityType<CrateBlockEntity> hyperCrateBlockEntityType;

	private static CrateBlockEntity hyperCrateBe(BlockPos pos, BlockState state) {
		return new CrateBlockEntity(hyperCrateBlockEntityType, pos, state, () -> new FlexibleDiscreteStore(Long.MAX_VALUE).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "HYPERCRATE ");
	}

	public static void initialize() {
		final var crateBlock = Facility.blockNoItem("crate", new CrateBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), CrateBlocks::crateBe));
		crateBlockEntityType = Facility.blockEntityType("crate", CrateBlocks::crateBe, crateBlock);
		final var portableCrateItem = Facility.item("crate_item", new PortableCrateItem(crateBlock, Facility.itemSettings().stacksTo(1).durability(2048), () -> new FlexibleDiscreteStore(2048).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		Facility.blockItem("crate", new BlockItem(crateBlock, Facility.itemSettings()));

		crateBlock.portableItem = portableCrateItem;

		final var slottedCrateBlock = Facility.blockNoItem("slotted_crate", new CrateBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), CrateBlocks::slottedBe));
		slottedCrateBlockEntityType = Facility.blockEntityType("slotted_crate", CrateBlocks::slottedBe, slottedCrateBlock);
		final var portableSlottedCrateItem = Facility.item("slotted_crate_item", new PortableCrateItem(slottedCrateBlock, Facility.itemSettings().stacksTo(1).durability(2048), () -> new SlottedInventoryStore(32).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		Facility.blockItem("slotted_crate", new BlockItem(slottedCrateBlock, Facility.itemSettings()));
		slottedCrateBlock.portableItem = portableSlottedCrateItem;

		final var creativeCrateBlock = Facility.block("creative_crate", new CreativeCrateBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), CrateBlocks::itemSupplier));
		creativeCrateBlockEntityType = Facility.blockEntityType("creative_crate", CrateBlocks::itemSupplier, creativeCrateBlock);

		final var hyperCrateBlock = Facility.blockNoItem("hyper_crate", new CrateBlock(Block.Properties.of(Material.METAL).strength(1, 1), CrateBlocks::hyperCrateBe));
		hyperCrateBlockEntityType = Facility.blockEntityType("hyper_crate", CrateBlocks::hyperCrateBe, hyperCrateBlock);
		final var portableHyperCrateItem = Facility.item("hyper_crate_item", new PortableCrateItem(hyperCrateBlock, Facility.itemSettings().stacksTo(1).durability(2048), () -> new FlexibleDiscreteStore(Long.MAX_VALUE).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		Facility.blockItem("hyper_crate", new BlockItem(hyperCrateBlock, Facility.itemSettings()));
		hyperCrateBlock.portableItem = portableHyperCrateItem;

		//CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(CRATE, SLOTTED_CRATE, CREATIVE_CRATE, HYPER_CRATE);

		Store.STORAGE_COMPONENT.registerProvider(ctx -> Store.CREATIVE, creativeCrateBlock);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> Store.CREATIVE, creativeCrateBlock);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> Store.CREATIVE.getConsumer(), crateBlock, slottedCrateBlock, hyperCrateBlock);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> Store.CREATIVE.getSupplier(), crateBlock, slottedCrateBlock, hyperCrateBlock);

		Store.STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage(), crateBlock, slottedCrateBlock, hyperCrateBlock);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getInternalStorage(), crateBlock, slottedCrateBlock, hyperCrateBlock);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage().getConsumer(), crateBlock, slottedCrateBlock, hyperCrateBlock);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage().getSupplier(), crateBlock, slottedCrateBlock, hyperCrateBlock);

		final XmPaint basePaint = Textures.crateBaseFinder(2).find();

		XmBlockRegistry.addBlockStates(slottedCrateBlock, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState().paintAll(Textures.cratePaintWithDecal(Textures.OPEN_BOX, 0xA0402918)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(crateBlock, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState().paintAll(basePaint), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(creativeCrateBlock, bs -> PrimitiveStateFunction.builder()
				.withDefaultState(Cube.INSTANCE.newState().paintAll(Textures.crateBaseFinder(2).textureColor(0, 0xFF00FFFF).find()))
				.build());

		XmBlockRegistry.addBlockStates(hyperCrateBlock, bs -> PrimitiveStateFunction.builder()
				.withDefaultState(Cube.INSTANCE.newState().paintAll(
						Textures.crateBaseFinder(3)
						.texture(2, Textures.FILLED_BOX)
						.textureColor(2, 0xFF80FFFF)
						.disableAo(2, true)
						.disableDiffuse(2, true)
						.emissive(2, true)
						.find()))
				.build());

		XmItemRegistry.addItem(portableCrateItem, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION_V2);
		XmItemRegistry.addItem(portableSlottedCrateItem, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION_V2);
		XmItemRegistry.addItem(portableHyperCrateItem, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION_V2);
	}
}
