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

import net.fabricmc.fabric.api.block.FabricBlockSettings;

import grondag.facility.storage.CrateBlock;
import grondag.facility.storage.CrateBlockEntity;
import grondag.facility.storage.CreativeCrateBlockEntity;
import grondag.facility.storage.CreativeICrateBlock;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.storage.discrete.FlexibleDiscreteStorage;
import grondag.fluidity.base.storage.discrete.SlottedInventoryStorage;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.Cube;
import grondag.xm.api.primitive.simple.CubeWithFace;

@SuppressWarnings("unchecked")
public enum CrateBlocks {
	;

	public static final CrateBlock CRATE = REG.block("crate", new CrateBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), CrateBlocks::crateBe));
	public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY_TYPE = REG.blockEntityType("crate", CrateBlocks::crateBe, CRATE);
	static CrateBlockEntity crateBe() {
		return new CrateBlockEntity(CRATE_BLOCK_ENTITY_TYPE, () -> new FlexibleDiscreteStorage(2048).filter(FILTER_NESTING), "CRATE ");
	}


	public static final CrateBlock BARREL = REG.block("barrel", new CrateBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), CrateBlocks::barrelBe));
	public static final BlockEntityType<CrateBlockEntity> BARREL_BLOCK_ENTITY_TYPE = REG.blockEntityType("barrel", CrateBlocks::barrelBe, BARREL);
	static CrateBlockEntity barrelBe() {
		return new CrateBlockEntity(BARREL_BLOCK_ENTITY_TYPE, () -> new SlottedInventoryStorage(32).filter(FILTER_NESTING), "BARREL ");
	}


	public static final CreativeICrateBlock ITEM_SUPPLIER = REG.block("item_supplier", new CreativeICrateBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), CrateBlocks::itemSupplier));
	public static final BlockEntityType<CreativeCrateBlockEntity> ITEM_SUPPLIER_BLOCK_ENTITY_TYPE = REG.blockEntityType("item_supplier", CrateBlocks::itemSupplier, ITEM_SUPPLIER);
	static CreativeCrateBlockEntity itemSupplier() {
		return new CreativeCrateBlockEntity(ITEM_SUPPLIER_BLOCK_ENTITY_TYPE, true);
	}

	public static final Predicate<Article> FILTER_NESTING = d -> !d.hasTag() || Block.getBlockFromItem(d.toItem()).getClass() != CrateBlock.class;

	static {
		CarrierConnector.CARRIER_CONNECTOR_COMPONENT.addProvider(CRATE, BARREL, ITEM_SUPPLIER);

		Storage.STORAGE_COMPONENT.addProvider(ctx -> Storage.CREATIVE, ITEM_SUPPLIER);
		Storage.INTERNAL_STORAGE_COMPONENT.addProvider(ctx -> Storage.CREATIVE, ITEM_SUPPLIER);
		Storage.STORAGE_COMPONENT.addProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getEffectiveStorage(), CRATE, BARREL);
		Storage.INTERNAL_STORAGE_COMPONENT.addProvider(ctx -> ((CrateBlockEntity) ctx.blockEntity()).getInternalStorage(), CRATE, BARREL);

		final XmPaint basePaint = Textures.crateBaseFinder(2).find();

		XmBlockRegistry.addBlockStates(BARREL, bs -> PrimitiveStateFunction.builder()
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

		XmBlockRegistry.addBlockStates(ITEM_SUPPLIER, bs -> PrimitiveStateFunction.builder()
				.withDefaultState(Cube.INSTANCE.newState().paintAll(Textures.crateBaseFinder(2).textureColor(0, 0xFF00FFFF).find()))
				.build());
	}
}
