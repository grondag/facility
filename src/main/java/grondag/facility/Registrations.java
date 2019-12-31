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
package grondag.facility;

import static grondag.facility.Facility.REG;
import static grondag.xm.api.texture.TextureGroup.STATIC_TILES;
import static grondag.xm.api.texture.TextureRenderIntent.BASE_ONLY;
import static grondag.xm.api.texture.TextureScale.SINGLE;
import static grondag.xm.api.texture.TextureTransform.IDENTITY;
import static grondag.xm.api.texture.TextureTransform.ROTATE_RANDOM;
import static grondag.xm.api.texture.TextureTransform.STONE_LIKE;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import grondag.facility.block.BinBlockEntity;
import grondag.facility.block.BinStorageBlock;
import grondag.facility.block.CreativeBlockEntity;
import grondag.facility.block.CreativeStorageBlock;
import grondag.facility.block.ItemStorageBlock;
import grondag.facility.block.ItemStorageBlockEntity;
import grondag.facility.block.ItemStorageContainer;
import grondag.facility.block.PipeBlock;
import grondag.facility.block.PipeBlockEntity;
import grondag.facility.block.PipeModel;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.storage.discrete.DividedDiscreteStorage;
import grondag.fluidity.base.storage.discrete.FlexibleDiscreteStorage;
import grondag.fluidity.base.storage.discrete.SlottedInventoryStorage;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.PaintBlendMode;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.paint.XmPaintFinder;
import grondag.xm.api.primitive.simple.Cube;
import grondag.xm.api.primitive.simple.CubeWithFace;
import grondag.xm.api.texture.TextureLayoutMap;
import grondag.xm.api.texture.TextureSet;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.texture.TextureSetHelper;

@SuppressWarnings("unchecked")
public enum Registrations {
	;

	public static final ItemStorageBlock CRATE = REG.block("crate", new ItemStorageBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), Registrations::crateBe));
	public static final ItemStorageBlock BARREL = REG.block("barrel", new ItemStorageBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), Registrations::barrelBe));
	public static final BinStorageBlock BIN_X1 = REG.block("bin_x1", new BinStorageBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), Registrations::binX1Be, 1));
	public static final BinStorageBlock BIN_X2 = REG.block("bin_x2", new BinStorageBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), Registrations::binX2Be, 2));
	public static final BinStorageBlock BIN_X4 = REG.block("bin_x4", new BinStorageBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), Registrations::binX4Be, 4));
	public static final CreativeStorageBlock ITEM_SUPPLIER = REG.block("item_supplier", new CreativeStorageBlock(FabricBlockSettings.of(Material.WOOD).strength(1, 1).build(), Registrations::itemSupplier));
	public static final PipeBlock PIPE = REG.block("basic_pipe", new PipeBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().strength(1, 1).build(), Registrations::pipeSupplier));

	public static final BlockEntityType<ItemStorageBlockEntity> CRATE_BLOCK_ENTITY_TYPE = REG.blockEntityType("crate", Registrations::crateBe, CRATE);
	public static final BlockEntityType<ItemStorageBlockEntity> BARREL_BLOCK_ENTITY_TYPE = REG.blockEntityType("barrel", Registrations::barrelBe, BARREL);
	public static final BlockEntityType<BinBlockEntity> BIN_X1_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x1", Registrations::binX1Be, BIN_X1);
	public static final BlockEntityType<BinBlockEntity> BIN_X2_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x2", Registrations::binX2Be, BIN_X2);
	public static final BlockEntityType<BinBlockEntity> BIN_X4_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x4", Registrations::binX4Be, BIN_X4);
	public static final BlockEntityType<CreativeBlockEntity> ITEM_SUPPLIER_BLOCK_ENTITY_TYPE = REG.blockEntityType("item_supplier", Registrations::itemSupplier, ITEM_SUPPLIER);
	public static final BlockEntityType<PipeBlockEntity> PIPE_BLOCK_ENTITY_TYPE = REG.blockEntityType("basic_pipe", Registrations::pipeSupplier, PIPE);

	public static final Predicate<Article> FILTER_NESTING = d -> !d.hasTag() || Block.getBlockFromItem(d.toItem()).getClass() != ItemStorageBlock.class;

	static ItemStorageBlockEntity crateBe() {
		return new ItemStorageBlockEntity(CRATE_BLOCK_ENTITY_TYPE, () -> new FlexibleDiscreteStorage(2048).filter(FILTER_NESTING), "CRATE ");
	}

	static ItemStorageBlockEntity barrelBe() {
		return new ItemStorageBlockEntity(BARREL_BLOCK_ENTITY_TYPE, () -> new SlottedInventoryStorage(32).filter(FILTER_NESTING), "BARREL ");
	}

	static BinBlockEntity binX1Be() {
		return new BinBlockEntity(BIN_X1_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStorage(1, 2048).filter(FILTER_NESTING), "BINx1 ", 1);
	}

	static BinBlockEntity binX2Be() {
		return new BinBlockEntity(BIN_X2_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStorage(2, 1024).filter(FILTER_NESTING), "BINx2 ", 2);
	}

	static BinBlockEntity binX4Be() {
		return new BinBlockEntity(BIN_X4_BLOCK_ENTITY_TYPE, () -> new DividedDiscreteStorage(4, 512).filter(FILTER_NESTING), "BINx4 ", 4);
	}

	static CreativeBlockEntity itemSupplier() {
		return new CreativeBlockEntity(ITEM_SUPPLIER_BLOCK_ENTITY_TYPE, true);
	}

	static PipeBlockEntity pipeSupplier() {
		return new PipeBlockEntity(PIPE_BLOCK_ENTITY_TYPE);
	}

	public static final TextureSet CRATE_BASE = TextureSet.builder()
			.displayNameToken("crate_base").baseTextureName("facility:block/crate_base")
			.versionCount(4).scale(SINGLE).layout(TextureLayoutMap.VERSIONED).transform(STONE_LIKE)
			.renderIntent(BASE_ONLY).groups(STATIC_TILES).build("contained:crate_base");

	public static final TextureSet OPEN_BOX = TextureSetHelper.addDecal(Facility.MODID, "open_box", "open_box", ROTATE_RANDOM);
	public static final TextureSet FILLED_BOX = TextureSetHelper.addDecal(Facility.MODID, "filled_box", "filled_box", ROTATE_RANDOM);
	public static final TextureSet BIN_FACE = TextureSetHelper.addDecal(Facility.MODID, "bin_face", "bin_face", IDENTITY);
	public static final TextureSet HALF_DIVIDER = TextureSetHelper.addDecal(Facility.MODID, "half_divider", "half_divider", STONE_LIKE);
	public static final TextureSet QUARTER_DIVIDER = TextureSetHelper.addDecal(Facility.MODID, "quarter_divider", "quarter_divider", STONE_LIKE);

	static {
		final XmPaint basePaint = crateBaseFinder(2).find();

		XmBlockRegistry.addBlockStates(BARREL, bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemStorageBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState().paintAll(cratePaintWithDecal(OPEN_BOX, 0xA0402918)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(CRATE, bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemStorageBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState().paintAll(basePaint), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(BIN_X1, bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemStorageBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, cratePaintWithDecal(BIN_FACE, 0xFFFFFFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(BIN_X2, bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemStorageBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, cratePaintWithDecal(HALF_DIVIDER, 0xFFFFFFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(BIN_X4, bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemStorageBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paintAll(basePaint)
						.paint(CubeWithFace.SURFACE_TOP, cratePaintWithDecal(QUARTER_DIVIDER, 0xFFFFFFFF)), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(ITEM_SUPPLIER, bs -> PrimitiveStateFunction.builder()
				.withDefaultState(Cube.INSTANCE.newState().paintAll(crateBaseFinder(2).textureColor(0, 0xFF00FFFF).find()))
				.build());

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

		ContainerProviderRegistry.INSTANCE.registerFactory(ItemStorageContainer.ID, (syncId, identifier, player, buf) ->  {
			final BlockPos pos = buf.readBlockPos();
			final String label = buf.readString();
			final World world = player.getEntityWorld();
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof ItemStorageBlockEntity) {
				final ItemStorageBlockEntity myBe = (ItemStorageBlockEntity) be;
				return new ItemStorageContainer(player, syncId, world.isClient ? null : myBe.getComponent(Storage.STORAGE_COMPONENT), label);
			}

			return null;
		});


	}

	static XmPaint cratePaintWithDecal(TextureSet decal, int color) {
		return crateBaseFinder(3)
				.texture(2, decal)
				.blendMode(2, PaintBlendMode.TRANSLUCENT)
				.textureColor(2, color)
				.find();
	}

	static XmPaintFinder crateBaseFinder(int depth) {
		return XmPaint.finder()
				.textureDepth(depth)
				.texture(0, CRATE_BASE)
				.textureColor(0, 0xFFFFFFFF)
				.texture(1, XmTextures.BORDER_WEATHERED_LINE)
				.blendMode(1, PaintBlendMode.TRANSLUCENT)
				.textureColor(1, 0xA0000000);
	}
}
