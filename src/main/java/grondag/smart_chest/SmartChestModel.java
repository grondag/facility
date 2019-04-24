package grondag.smart_chest;

import static grondag.smart_chest.SmartChest.MODID;
import static net.minecraft.block.BlockRenderLayer.SOLID;
import static net.minecraft.block.BlockRenderLayer.TRANSLUCENT;

import java.util.function.Supplier;

import grondag.frex.api.Renderer;
import grondag.frex.api.RendererAccess;
import grondag.frex.api.material.RenderMaterial;
import grondag.frex.api.mesh.MeshBuilder;
import grondag.frex.api.mesh.MutableQuadView;
import grondag.frex.api.mesh.QuadEmitter;
import grondag.frex.api.model.ModelHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SmartChestModel implements Supplier<BakedModel> {

    @Override
    public BakedModel get() {
        final Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        final MeshBuilder mb = renderer.meshBuilder();
        final QuadEmitter qe = mb.getEmitter();
        final RenderMaterial mat = renderer.materialFinder().spriteDepth(3)
            .blendMode(0, SOLID)
            .blendMode(1, TRANSLUCENT).disableAo(1, true).disableDiffuse(1, true).emissive(1, true)
            .blendMode(2, TRANSLUCENT).disableAo(2, true).disableDiffuse(2, true).emissive(2, true)
            .find();
        final int baseColor = 0xFF50565D;
        final int borderColor = 0xFF9292B1;
        final int chestColor = 0xFFD4DFF1;
        
        SpriteAtlasTexture atlas = MinecraftClient.getInstance().getSpriteAtlas();
        final Sprite spriteBase = atlas.getSprite(new Identifier(MODID, "block/noise_moderate_0_0"));
        final Sprite spriteBorder = atlas.getSprite(new Identifier(MODID, "block/border_filmstrip_0_4"));
        final Sprite spriteChest = atlas.getSprite(new Identifier(MODID, "block/symbol_chest"));
        
        qe.material(mat)
                .square(Direction.UP,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat)
                .square(Direction.DOWN,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat)
                .square(Direction.EAST,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat)
                .square(Direction.WEST,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat)
                .square(Direction.NORTH,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat)
                .square(Direction.SOUTH,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        return new SimpleModel(mb.build(), null, spriteBase, ModelHelper.MODEL_TRANSFORM_BLOCK, null);
    }
}
