package grondag.contained;

import static grondag.contained.Contained.REG;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import grondag.contained.block.ItemStorageBlock;
import grondag.contained.block.ItemStorageBlockEntity;
import grondag.contained.block.ItemStorageContainer;
import grondag.fluidity.base.storage.FlexibleItemStorage;
import grondag.fluidity.base.storage.SimpleItemStorage;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.PaintBlendMode;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.CubeWithFace;
import grondag.xm.api.texture.XmTextures;

@SuppressWarnings("unchecked")
public enum Registrations {
	;

	public static final ItemStorageBlock CRATE = REG.block("crate", new ItemStorageBlock(Registrations::crateBe));
	public static final ItemStorageBlock BARREL = REG.block("barrel", new ItemStorageBlock(Registrations::barrelBe));

	public static final BlockEntityType<ItemStorageBlockEntity> CRATE_BLOCK_ENTITY_TYPE = REG.blockEntityType("crate", Registrations::crateBe, CRATE);
	public static final BlockEntityType<ItemStorageBlockEntity> BARREL_BLOCK_ENTITY_TYPE = REG.blockEntityType("barrel", Registrations::barrelBe, BARREL);

	static ItemStorageBlockEntity crateBe() {
		return new ItemStorageBlockEntity(CRATE_BLOCK_ENTITY_TYPE, new FlexibleItemStorage(2048), "CRATE ");
	}

	static ItemStorageBlockEntity barrelBe() {
		return new ItemStorageBlockEntity(BARREL_BLOCK_ENTITY_TYPE, new SimpleItemStorage(32), "BARREL ");
	}

	static {
		final XmPaint frontPaint = XmPaint.finder()
				.textureDepth(1)
				.texture(0, XmTextures.TILE_NOISE_SUBTLE)
				.textureColor(0, 0xFF101015)

				.find();

		final XmPaint sidePaint = XmPaint.finder()
				.textureDepth(2)
				.texture(0, XmTextures.TILE_NOISE_MODERATE)
				.textureColor(0, 0xFF505060)
				.texture(1, XmTextures.BORDER_DOUBLE_PINSTRIPES)
				.blendMode(1, PaintBlendMode.TRANSLUCENT)
				.textureColor(1, 0xFF808090)
				.find();

		XmBlockRegistry.addBlockStates(BARREL, bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemStorageBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paint(CubeWithFace.SURFACE_TOP, frontPaint)
						.paint(CubeWithFace.SURFACE_BOTTOM, sidePaint)
						.paint(CubeWithFace.SURFACE_SIDES, sidePaint), bs), bs))
				.build());

		XmBlockRegistry.addBlockStates(CRATE, bs -> PrimitiveStateFunction.builder()
				.withJoin(ItemStorageBlock.JOIN_TEST)
				.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
				.withUpdate(XmProperties.FACE_MODIFIER)
				.withDefaultState(XmProperties.FACE_MODIFIER.mutate(SpeciesProperty.SPECIES_MODIFIER.mutate(
						CubeWithFace.INSTANCE.newState()
						.paint(CubeWithFace.SURFACE_TOP, frontPaint)
						.paint(CubeWithFace.SURFACE_BOTTOM, sidePaint)
						.paint(CubeWithFace.SURFACE_SIDES, sidePaint), bs), bs))
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
}
