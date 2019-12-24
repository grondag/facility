package grondag.contained;

import static grondag.contained.Contained.REG;
import static grondag.xm.api.texture.TextureGroup.STATIC_TILES;
import static grondag.xm.api.texture.TextureRenderIntent.BASE_ONLY;
import static grondag.xm.api.texture.TextureScale.SINGLE;
import static grondag.xm.api.texture.TextureTransform.IDENTITY;
import static grondag.xm.api.texture.TextureTransform.ROTATE_RANDOM;
import static grondag.xm.api.texture.TextureTransform.STONE_LIKE;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import grondag.contained.block.BinBlockEntity;
import grondag.contained.block.BinStorageBlock;
import grondag.contained.block.ItemStorageBlock;
import grondag.contained.block.ItemStorageBlockEntity;
import grondag.contained.block.ItemStorageContainer;
import grondag.fluidity.api.item.DiscreteItem;
import grondag.fluidity.base.storage.discrete.DividedItemStorage;
import grondag.fluidity.base.storage.discrete.FlexibleItemStorage;
import grondag.fluidity.base.storage.discrete.SlottedItemStorage;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.PaintBlendMode;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.paint.XmPaintFinder;
import grondag.xm.api.primitive.simple.CubeWithFace;
import grondag.xm.api.texture.TextureLayoutMap;
import grondag.xm.api.texture.TextureSet;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.texture.TextureSetHelper;

@SuppressWarnings("unchecked")
public enum Registrations {
	;

	public static final ItemStorageBlock CRATE = REG.block("crate", new ItemStorageBlock(Registrations::crateBe));
	public static final ItemStorageBlock BARREL = REG.block("barrel", new ItemStorageBlock(Registrations::barrelBe));
	public static final BinStorageBlock BIN_X1 = REG.block("bin_x1", new BinStorageBlock(Registrations::binX1Be, 1));
	public static final BinStorageBlock BIN_X2 = REG.block("bin_x2", new BinStorageBlock(Registrations::binX2Be, 2));
	public static final BinStorageBlock BIN_X4 = REG.block("bin_x4", new BinStorageBlock(Registrations::binX4Be, 4));

	public static final BlockEntityType<ItemStorageBlockEntity> CRATE_BLOCK_ENTITY_TYPE = REG.blockEntityType("crate", Registrations::crateBe, CRATE);
	public static final BlockEntityType<ItemStorageBlockEntity> BARREL_BLOCK_ENTITY_TYPE = REG.blockEntityType("barrel", Registrations::barrelBe, BARREL);
	public static final BlockEntityType<BinBlockEntity> BIN_X1_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x1", Registrations::binX1Be, BIN_X1);
	public static final BlockEntityType<BinBlockEntity> BIN_X2_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x2", Registrations::binX2Be, BIN_X2);
	public static final BlockEntityType<BinBlockEntity> BIN_X4_BLOCK_ENTITY_TYPE = REG.blockEntityType("bin_x4", Registrations::binX4Be, BIN_X4);

	public static final Predicate<DiscreteItem> FILTER_NESTING = d -> !d.hasTag() || Block.getBlockFromItem(d.getItem()).getClass() != ItemStorageBlock.class;

	static ItemStorageBlockEntity crateBe() {
		return new ItemStorageBlockEntity(CRATE_BLOCK_ENTITY_TYPE, () -> new FlexibleItemStorage(2048).filter(FILTER_NESTING), "CRATE ");
	}

	static ItemStorageBlockEntity barrelBe() {
		return new ItemStorageBlockEntity(BARREL_BLOCK_ENTITY_TYPE, () -> new SlottedItemStorage(32).filter(FILTER_NESTING), "BARREL ");
	}

	static BinBlockEntity binX1Be() {
		return new BinBlockEntity(BIN_X1_BLOCK_ENTITY_TYPE, () -> new DividedItemStorage(1, 2048).filter(FILTER_NESTING), "BINx1 ", 1);
	}

	static BinBlockEntity binX2Be() {
		return new BinBlockEntity(BIN_X2_BLOCK_ENTITY_TYPE, () -> new DividedItemStorage(2, 1024).filter(FILTER_NESTING), "BINx2 ", 2);
	}

	static BinBlockEntity binX4Be() {
		return new BinBlockEntity(BIN_X4_BLOCK_ENTITY_TYPE, () -> new DividedItemStorage(4, 512).filter(FILTER_NESTING), "BINx4 ", 4);
	}

	public static final TextureSet CRATE_BASE = TextureSet.builder()
			.displayNameToken("crate_base").baseTextureName("contained:block/crate_base")
			.versionCount(4).scale(SINGLE).layout(TextureLayoutMap.VERSIONED).transform(STONE_LIKE)
			.renderIntent(BASE_ONLY).groups(STATIC_TILES).build("contained:crate_base");

	public static final TextureSet OPEN_BOX = TextureSetHelper.addDecal(Contained.MODID, "open_box", "open_box", ROTATE_RANDOM);
	public static final TextureSet FILLED_BOX = TextureSetHelper.addDecal(Contained.MODID, "filled_box", "filled_box", ROTATE_RANDOM);
	public static final TextureSet BIN_FACE = TextureSetHelper.addDecal(Contained.MODID, "bin_face", "bin_face", IDENTITY);
	public static final TextureSet HALF_DIVIDER = TextureSetHelper.addDecal(Contained.MODID, "half_divider", "half_divider", STONE_LIKE);
	public static final TextureSet QUARTER_DIVIDER = TextureSetHelper.addDecal(Contained.MODID, "quarter_divider", "quarter_divider", STONE_LIKE);

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

		ContainerProviderRegistry.INSTANCE.registerFactory(ItemStorageContainer.ID, (syncId, identifier, player, buf) ->  {
			final BlockPos pos = buf.readBlockPos();
			final String label = buf.readString();
			final World world = player.getEntityWorld();
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof ItemStorageBlockEntity) {
				final ItemStorageBlockEntity myBe = (ItemStorageBlockEntity) be;
				return new ItemStorageContainer(player, syncId, world.isClient ? null : myBe.getDiscreteStorage(), label);
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
