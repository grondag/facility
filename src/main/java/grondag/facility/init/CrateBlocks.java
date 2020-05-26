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

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import grondag.facility.storage.item.CrateBlock;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.facility.storage.item.CreativeCrateBlock;
import grondag.facility.storage.item.CreativeCrateBlockEntity;
import grondag.facility.storage.item.PortableCrateItem;
import grondag.facility.storage.item.SlottedCrateBlockEntity;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.discrete.FlexibleDiscreteStore;
import grondag.fluidity.base.storage.discrete.SlottedInventoryStore;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.item.XmItemRegistry;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.PaintBlendMode;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.Cube;
import grondag.xm.api.primitive.simple.CubeWithFace;

@SuppressWarnings("unchecked")
public enum CrateBlocks {
	;

	public static final Predicate<Article> FILTER_TYPE_AND_NESTING = d -> d.type() == ArticleType.ITEM && (!d.hasTag() || Block.getBlockFromItem(d.toItem()).getClass() != CrateBlock.class);

	public static final CrateBlock CRATE = REG.blockNoItem("crate", new CrateBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1), CrateBlocks::crateBe));
	public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY_TYPE = REG.blockEntityType("crate", CrateBlocks::crateBe, CRATE);
	static CrateBlockEntity crateBe() {
		return new CrateBlockEntity(CRATE_BLOCK_ENTITY_TYPE, () -> new FlexibleDiscreteStore(2048).filter(FILTER_TYPE_AND_NESTING), "CRATE ");
	}
	public static final PortableCrateItem PORTABLE_CRATE_ITEM = REG.item("crate_item", new PortableCrateItem(CRATE, REG.itemSettings().maxCount(1).maxDamage(2048), () -> new FlexibleDiscreteStore(2048).filter(FILTER_TYPE_AND_NESTING)));
	public static final Item CRATE_ITEM = REG.item("crate", new BlockItem(CRATE, REG.itemSettings()));


	public static final CrateBlock SLOTTED_CRATE = REG.blockNoItem("slotted_crate", new CrateBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1), CrateBlocks::slottedBe));
	public static final BlockEntityType<SlottedCrateBlockEntity> SLOTTED_CRATE_BLOCK_ENTITY_TYPE = REG.blockEntityType("slotted_crate", CrateBlocks::slottedBe, SLOTTED_CRATE);
	static SlottedCrateBlockEntity slottedBe() {
		return new SlottedCrateBlockEntity(SLOTTED_CRATE_BLOCK_ENTITY_TYPE, () -> new SlottedInventoryStore(32).filter(FILTER_TYPE_AND_NESTING), "SLOTTED CRATE ");
	}
	public static final PortableCrateItem PORTABLE_SLOTTED_CRATE_ITEM = REG.item("slotted_crate_item", new PortableCrateItem(SLOTTED_CRATE, REG.itemSettings().maxCount(1).maxDamage(2048), () -> new SlottedInventoryStore(32).filter(FILTER_TYPE_AND_NESTING)));
	public static final Item SLOTTED_CRATE_ITEM = REG.item("slotted_crate", new BlockItem(SLOTTED_CRATE, REG.itemSettings()));


	public static final CreativeCrateBlock CREATIVE_CRATE = REG.block("creative_crate", new CreativeCrateBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1), CrateBlocks::itemSupplier));
	public static final BlockEntityType<CreativeCrateBlockEntity> CREATIVE_CRATE_BLOCK_ENTITY_TYPE = REG.blockEntityType("creative_crate", CrateBlocks::itemSupplier, CREATIVE_CRATE);
	static CreativeCrateBlockEntity itemSupplier() {
		return new CreativeCrateBlockEntity(CREATIVE_CRATE_BLOCK_ENTITY_TYPE, true);
	}


	public static final CrateBlock HYPER_CRATE = REG.blockNoItem("hyper_crate", new CrateBlock(FabricBlockSettings.of(Material.METAL).strength(1, 1), CrateBlocks::hyperCrateBe));
	public static final BlockEntityType<CrateBlockEntity> HYPER_CRATE_BLOCK_ENTITY_TYPE = REG.blockEntityType("hyper_crate", CrateBlocks::hyperCrateBe, HYPER_CRATE);
	static CrateBlockEntity hyperCrateBe() {
		return new CrateBlockEntity(HYPER_CRATE_BLOCK_ENTITY_TYPE, () -> new FlexibleDiscreteStore(Long.MAX_VALUE).filter(FILTER_TYPE_AND_NESTING), "HYPERCRATE ");
	}
	public static final PortableCrateItem PORTABLE_HYPER_CRATE_ITEM = REG.item("hyper_crate_item", new PortableCrateItem(HYPER_CRATE, REG.itemSettings().maxCount(1).maxDamage(2048), () -> new FlexibleDiscreteStore(Long.MAX_VALUE).filter(FILTER_TYPE_AND_NESTING)));
	public static final Item HYPER_CRATE_ITEM = REG.item("hyper_crate", new BlockItem(HYPER_CRATE, REG.itemSettings()));

	static {
		CRATE.portableItem = PORTABLE_CRATE_ITEM;
		SLOTTED_CRATE.portableItem = PORTABLE_SLOTTED_CRATE_ITEM;
		HYPER_CRATE.portableItem = PORTABLE_HYPER_CRATE_ITEM;

		CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(CRATE, SLOTTED_CRATE, CREATIVE_CRATE, HYPER_CRATE);

		Store.STORAGE_COMPONENT.registerProvider(ctx -> Store.CREATIVE, CREATIVE_CRATE);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> Store.CREATIVE, CREATIVE_CRATE);
		Store.STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage(), CRATE, SLOTTED_CRATE, HYPER_CRATE);
		Store.INTERNAL_STORAGE_COMPONENT.registerProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getInternalStorage(), CRATE, SLOTTED_CRATE, HYPER_CRATE);

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
						.blendMode(2, PaintBlendMode.TRANSLUCENT)
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
