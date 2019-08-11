package grondag.smart_chest;

import static grondag.smart_chest.SmartChest.MODID;
import static net.minecraft.block.BlockRenderLayer.SOLID;
import static net.minecraft.block.BlockRenderLayer.TRANSLUCENT;

import java.util.Random;
import java.util.function.Supplier;

import grondag.brocade.painting.CubicQuadPainterBorders;
import grondag.frex.api.Renderer;
import grondag.frex.api.material.MaterialFinder;
import grondag.xm.api.connect.state.CornerJoinState;
import grondag.xm.api.connect.world.BlockNeighbors;
import grondag.xm.api.connect.world.BlockTest;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;

public class SmartChestModel implements Supplier<BakedModel> {

    private static RenderMaterial mat2;
    private static RenderMaterial mat3;
    private static final int baseColor = 0xFF50565D;
    private static final int borderColor = 0xFFF12D3A; //0xFF5C6570;
//    private static final int chestColor = 0xFFD4DFF1;
    
    @Override
    public BakedModel get() {
        final Renderer renderer = (Renderer) RendererAccess.INSTANCE.getRenderer();
        final MeshBuilder mb = renderer.meshBuilder();
        final QuadEmitter qe = mb.getEmitter();
        final MaterialFinder finder = renderer.materialFinder();
        mat2 = finder.spriteDepth(2)
                .blendMode(0, SOLID)
                .blendMode(1, TRANSLUCENT).disableAo(1, true).disableDiffuse(1, true).emissive(1, true)
                .find();
        mat3 = finder.spriteDepth(3)
            .blendMode(2, TRANSLUCENT).disableAo(2, true).disableDiffuse(2, true).emissive(2, true)
            .find();
        
        SpriteAtlasTexture atlas = MinecraftClient.getInstance().getSpriteAtlas();
        final Sprite spriteBase = atlas.getSprite(new Identifier(MODID, "block/noise_moderate_0_0"));
        final Sprite spriteBorder = atlas.getSprite(new Identifier(MODID, "block/border_signal_0_4"));
//        final Sprite spriteChest = atlas.getSprite(new Identifier(MODID, "block/symbol_chest"));
        
        qe.material(mat2)
                .square(Direction.UP,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
//                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
//                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat2)
                .square(Direction.DOWN,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
//                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
//                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat2)
                .square(Direction.EAST,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
//                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
//                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat2)
                .square(Direction.WEST,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
//                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
//                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat3)
                .square(Direction.NORTH,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
//                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
//                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                .emit();
        
        qe.material(mat2)
                .square(Direction.SOUTH,  0, 0, 1, 1, 0)
                .spriteBake(0, spriteBase, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteBake(1, spriteBorder, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
//                .spriteBake(2, spriteChest, MutableQuadView.BAKE_LOCK_UV | MutableQuadView.BAKE_NORMALIZED)
                .spriteColor(0, baseColor, baseColor, baseColor, baseColor)
                .spriteColor(1, borderColor, borderColor, borderColor, borderColor)
//                .spriteColor(2, chestColor, chestColor, chestColor, chestColor)
                
                .emit();
        
        return new SimpleModel(mb.build(), transformers::get, spriteBase, ModelHelper.MODEL_TRANSFORM_BLOCK, null);
    }
    
    private static final ThreadLocal<Transformer> transformers = ThreadLocal.withInitial(Transformer::new);
    
    public static class Transformer implements MeshTransformer {
        private CornerJoinState cjs;
        
        @Override
        public boolean transform(MutableQuadView quad) {
            if(cjs != null) {
                CubicQuadPainterBorders.bakeBorderSprite(quad, 1, cjs);
//                if(!CubicQuadPainterBorders.bakeBorderSprite(quad, 1, cjbs)) {
//                    quad.sprite(0, 1, quad.spriteU(0, 2), quad.spriteV(0, 2))
//                    .sprite(1, 1, quad.spriteU(1, 2), quad.spriteV(1, 2))
//                    .sprite(2, 1, quad.spriteU(2, 2), quad.spriteV(2, 2))
//                    .sprite(3, 1, quad.spriteU(3, 2), quad.spriteV(3, 2))
//                    .spriteColor(1, quad.spriteColor(0, 2), quad.spriteColor(1, 2), quad.spriteColor(2, 2), quad.spriteColor(3, 2))
//                    .material(mat2);
//                }
            }
            return true;
        }

        @SuppressWarnings("rawtypes")
        static final BlockTest MATCHER = (c) -> c.fromBlockState() != null && c.fromBlockState().equals(c.toBlockState());
        
        @Override
        public MeshTransformer prepare(ExtendedBlockView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier) {
            cjs = CornerJoinState.fromWorld(BlockNeighbors.threadLocal(blockView, pos, MATCHER));
            return this;
        }

        @Override
        public MeshTransformer prepare(ItemStack stack, Supplier<Random> randomSupplier) {
            cjs = null;
            return this;
        }
        
    }
}
