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

		if (d.hasTag() && Facility.STORAGE_BLACKLIST_WITH_CONTENT.contains(item)) return false;

		if (Facility.STORAGE_BLACKLIST_ALWAYS.contains(item)) return false;

		return true;
	};

	private static CrateBlock CRATE;
	private static BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY_TYPE;

	public static BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY_TYPE() {
		return CRATE_BLOCK_ENTITY_TYPE;
	}

	private static CrateBlockEntity crateBe(BlockPos pos, BlockState state) {
		return new CrateBlockEntity(CRATE_BLOCK_ENTITY_TYPE, pos, state, () -> new FlexibleDiscreteStore(2048).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "CRATE ");
	}
	private static PortableCrateItem PORTABLE_CRATE_ITEM;
	private static Item CRATE_ITEM;

	private static CrateBlock SLOTTED_CRATE;
	private static BlockEntityType<SlottedCrateBlockEntity> SLOTTED_CRATE_BLOCK_ENTITY_TYPE;

	public static BlockEntityType<SlottedCrateBlockEntity> SLOTTED_CRATE_BLOCK_ENTITY_TYPE() {
		return SLOTTED_CRATE_BLOCK_ENTITY_TYPE;
	}

	private static SlottedCrateBlockEntity slottedBe(BlockPos pos, BlockState state) {
		return new SlottedCrateBlockEntity(SLOTTED_CRATE_BLOCK_ENTITY_TYPE, pos, state, () -> new SlottedInventoryStore(32).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "SLOTTED CRATE ");
	}
	private static PortableCrateItem PORTABLE_SLOTTED_CRATE_ITEM;
	private static Item SLOTTED_CRATE_ITEM;

	private static CreativeCrateBlock CREATIVE_CRATE;
	private static BlockEntityType<CreativeCrateBlockEntity> CREATIVE_CRATE_BLOCK_ENTITY_TYPE;

	private static CreativeCrateBlockEntity itemSupplier(BlockPos pos, BlockState state) {
		return new CreativeCrateBlockEntity(CREATIVE_CRATE_BLOCK_ENTITY_TYPE, pos, state, true);
	}

	private static CrateBlock HYPER_CRATE;
	private static BlockEntityType<CrateBlockEntity> HYPER_CRATE_BLOCK_ENTITY_TYPE;

	private static CrateBlockEntity hyperCrateBe(BlockPos pos, BlockState state) {
		return new CrateBlockEntity(HYPER_CRATE_BLOCK_ENTITY_TYPE, pos, state, () -> new FlexibleDiscreteStore(Long.MAX_VALUE).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()), "HYPERCRATE ");
	}
	private static PortableCrateItem PORTABLE_HYPER_CRATE_ITEM;
	private static Item HYPER_CRATE_ITEM;

	public static void initialize() {
		CRATE = Facility.blockNoItem("crate", new CrateBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), CrateBlocks::crateBe));
		CRATE_BLOCK_ENTITY_TYPE = Facility.blockEntityType("crate", CrateBlocks::crateBe, CRATE);
		PORTABLE_CRATE_ITEM = Facility.item("crate_item", new PortableCrateItem(CRATE, Facility.itemSettings().stacksTo(1).durability(2048), () -> new FlexibleDiscreteStore(2048).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		CRATE_ITEM = Facility.item("crate", new BlockItem(CRATE, Facility.itemSettings()));

		SLOTTED_CRATE = Facility.blockNoItem("slotted_crate", new CrateBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), CrateBlocks::slottedBe));
		SLOTTED_CRATE_BLOCK_ENTITY_TYPE = Facility.blockEntityType("slotted_crate", CrateBlocks::slottedBe, SLOTTED_CRATE);
		PORTABLE_SLOTTED_CRATE_ITEM = Facility.item("slotted_crate_item", new PortableCrateItem(SLOTTED_CRATE, Facility.itemSettings().stacksTo(1).durability(2048), () -> new SlottedInventoryStore(32).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		SLOTTED_CRATE_ITEM = Facility.item("slotted_crate", new BlockItem(SLOTTED_CRATE, Facility.itemSettings()));

		CREATIVE_CRATE = Facility.block("creative_crate", new CreativeCrateBlock(Block.Properties.of(Facility.CRATE_MATERIAL).strength(1, 1), CrateBlocks::itemSupplier));
		CREATIVE_CRATE_BLOCK_ENTITY_TYPE = Facility.blockEntityType("creative_crate", CrateBlocks::itemSupplier, CREATIVE_CRATE);

		HYPER_CRATE = Facility.blockNoItem("hyper_crate", new CrateBlock(Block.Properties.of(Material.METAL).strength(1, 1), CrateBlocks::hyperCrateBe));
		HYPER_CRATE_BLOCK_ENTITY_TYPE = Facility.blockEntityType("hyper_crate", CrateBlocks::hyperCrateBe, HYPER_CRATE);
		PORTABLE_HYPER_CRATE_ITEM = Facility.item("hyper_crate_item", new PortableCrateItem(HYPER_CRATE, Facility.itemSettings().stacksTo(1).durability(2048), () -> new FlexibleDiscreteStore(Long.MAX_VALUE).filter(FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate())));
		HYPER_CRATE_ITEM = Facility.item("hyper_crate", new BlockItem(HYPER_CRATE, Facility.itemSettings()));

		CRATE.portableItem = PORTABLE_CRATE_ITEM;
		SLOTTED_CRATE.portableItem = PORTABLE_SLOTTED_CRATE_ITEM;
		HYPER_CRATE.portableItem = PORTABLE_HYPER_CRATE_ITEM;

		//CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(CRATE, SLOTTED_CRATE, CREATIVE_CRATE, HYPER_CRATE);

		Store.STORAGE_COMPONENT.registerProvider(ctx -> Store.CREATIVE, CREATIVE_CRATE);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> Store.CREATIVE, CREATIVE_CRATE);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> Store.CREATIVE.getConsumer(), CRATE, SLOTTED_CRATE, HYPER_CRATE);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> Store.CREATIVE.getSupplier(), CRATE, SLOTTED_CRATE, HYPER_CRATE);

		Store.STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage(), CRATE, SLOTTED_CRATE, HYPER_CRATE);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getInternalStorage(), CRATE, SLOTTED_CRATE, HYPER_CRATE);
		ArticleFunction.CONSUMER_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage().getConsumer(), CRATE, SLOTTED_CRATE, HYPER_CRATE);
		ArticleFunction.SUPPLIER_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage().getSupplier(), CRATE, SLOTTED_CRATE, HYPER_CRATE);

		final XmPaint basePaint = Textures.crateBaseFinder(2).find();

		XmBlockRegistry.addBlockStates(SLOTTED_CRATE, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState().paintAll(Textures.cratePaintWithDecal(Textures.OPEN_BOX, 0xA0402918)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(CRATE, bs -> PrimitiveStateFunction.builder()
				.withJoin(CrateBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState().paintAll(basePaint), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(CREATIVE_CRATE, bs -> PrimitiveStateFunction.builder()
				.withDefaultState(Cube.INSTANCE.newState().paintAll(Textures.crateBaseFinder(2).textureColor(0, 0xFF00FFFF).find()))
				.build());

		XmBlockRegistry.addBlockStates(HYPER_CRATE, bs -> PrimitiveStateFunction.builder()
				.withDefaultState(Cube.INSTANCE.newState().paintAll(
						Textures.crateBaseFinder(3)
						.texture(2, Textures.FILLED_BOX)
						.textureColor(2, 0xFF80FFFF)
						.disableAo(2, true)
						.disableDiffuse(2, true)
						.emissive(2, true)
						.find()))
				.build());

		XmItemRegistry.addItem(PORTABLE_CRATE_ITEM, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION);
		XmItemRegistry.addItem(PORTABLE_SLOTTED_CRATE_ITEM, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION);
		XmItemRegistry.addItem(PORTABLE_HYPER_CRATE_ITEM, XmBlockRegistry.DEFAULT_ITEM_MODEL_FUNCTION);
	}
}
