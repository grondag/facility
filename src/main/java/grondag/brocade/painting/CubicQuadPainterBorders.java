package grondag.brocade.painting;

import static grondag.frex.api.mesh.MutableQuadView.BAKE_FLIP_U;
import static grondag.frex.api.mesh.MutableQuadView.BAKE_FLIP_V;
import static grondag.frex.api.mesh.MutableQuadView.BAKE_LOCK_UV;
import static grondag.frex.api.mesh.MutableQuadView.BAKE_NORMALIZED;
import static grondag.frex.api.mesh.MutableQuadView.BAKE_ROTATE_180;
import static grondag.frex.api.mesh.MutableQuadView.BAKE_ROTATE_270;
import static grondag.frex.api.mesh.MutableQuadView.BAKE_ROTATE_90;
import static grondag.frex.api.mesh.MutableQuadView.BAKE_ROTATE_NONE;

import grondag.brocade.world.CornerJoinBlockState;
import grondag.brocade.world.CornerJoinFaceState;
import grondag.frex.api.mesh.MutableQuadView;
import grondag.smart_chest.SmartChest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public abstract class CubicQuadPainterBorders  {
    protected final static FaceQuadInputs[][] FACE_INPUTS = new FaceQuadInputs[6][CornerJoinFaceState.values().length];

    /** Texture offsets */
    private final static int TEXTURE_BOTTOM_LEFT_RIGHT = 0;
    private final static int TEXTURE_BOTTOM_LEFT = 1;
    private final static int TEXTURE_LEFT_RIGHT = 2;
    private final static int TEXTURE_BOTTOM = 3;
    private final static int TEXTURE_JOIN_NONE = 4;
    private final static int TEXTURE_BOTTOM_LEFT_RIGHT_BR = 5;
    private final static int TEXTURE_BOTTOM_LEFT_RIGHT_BL_BR = 6;
    private final static int TEXTURE_BOTTOM_LEFT_BL = 7;
    private final static int TEXTURE_JOIN_ALL_TR = 8;
    private final static int TEXTURE_JOIN_ALL_TL_TR = 9;
    private final static int TEXTURE_JOIN_ALL_TR_BL = 10;
    private final static int TEXTURE_JOIN_ALL_TR_BL_BR = 11;
    private final static int TEXTURE_JOIN_ALL_ALL_CORNERS = 12;
    // this last one will be a blank texture unless this is a completed texture vs
    // just a border
    private final static int TEXTURE_JOIN_ALL_NO_CORNERS = 13;

    /**
     * Used only when a border is rendered in the solid layer. Declared at module
     * level so that we can check for it.
     */
    private final static FaceQuadInputs NO_BORDER = new FaceQuadInputs(TEXTURE_JOIN_ALL_NO_CORNERS,
            BAKE_ROTATE_NONE, false, false);

    // PERF - really only need two instances of the array - up and others
    static {
        for (Direction face : Direction.values()) {
            // First one will only be used if we are rendering in solid layer.
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_NO_CORNERS.ordinal()] = NO_BORDER;
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.NO_FACE.ordinal()] = null; // NULL FACE
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.NONE.ordinal()] = new FaceQuadInputs(TEXTURE_JOIN_NONE,
                    BAKE_ROTATE_NONE, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM.ordinal()] = new FaceQuadInputs(TEXTURE_BOTTOM,
                    BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.LEFT.ordinal()] = new FaceQuadInputs(TEXTURE_BOTTOM,
                    BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP.ordinal()] = new FaceQuadInputs(TEXTURE_BOTTOM,
                    BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.RIGHT.ordinal()] = new FaceQuadInputs(TEXTURE_BOTTOM,
                    BAKE_ROTATE_270, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM_LEFT_NO_CORNER.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_LEFT_NO_CORNER.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT, BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_RIGHT_NO_CORNER.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT, BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM_RIGHT_NO_CORNER.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT, BAKE_ROTATE_270, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM_LEFT_RIGHT_NO_CORNERS
                    .ordinal()] = new FaceQuadInputs(TEXTURE_BOTTOM_LEFT_RIGHT, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM_LEFT_NO_CORNERS.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT, BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_LEFT_RIGHT_NO_CORNERS.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT, BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM_RIGHT_NO_CORNERS.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT, BAKE_ROTATE_270, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.LEFT_RIGHT.ordinal()] = new FaceQuadInputs(
                    TEXTURE_LEFT_RIGHT, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM.ordinal()] = new FaceQuadInputs(
                    TEXTURE_LEFT_RIGHT, BAKE_ROTATE_90, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM_LEFT_RIGHT_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BR, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM_LEFT_RIGHT_BL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BR, BAKE_ROTATE_NONE, true, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM_LEFT_BL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BR, BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM_LEFT_TL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BR, BAKE_ROTATE_90, true, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_LEFT_RIGHT_TL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BR, BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_LEFT_RIGHT_TR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BR, BAKE_ROTATE_180, true, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM_RIGHT_TR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BR, BAKE_ROTATE_270, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM_RIGHT_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BR, BAKE_ROTATE_270, true, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM_LEFT_RIGHT_BL_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BL_BR, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM_LEFT_TL_BL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BL_BR, BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_LEFT_RIGHT_TL_TR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BL_BR, BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_BOTTOM_RIGHT_TR_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_RIGHT_BL_BR, BAKE_ROTATE_270, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM_LEFT_BL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_BL, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_LEFT_TL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_BL, BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.TOP_RIGHT_TR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_BL, BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.BOTTOM_RIGHT_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_BOTTOM_LEFT_BL, BAKE_ROTATE_270, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TR.ordinal()] = new FaceQuadInputs(TEXTURE_JOIN_ALL_TR,
                    BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_BR.ordinal()] = new FaceQuadInputs(TEXTURE_JOIN_ALL_TR,
                    BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_BL.ordinal()] = new FaceQuadInputs(TEXTURE_JOIN_ALL_TR,
                    BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TL.ordinal()] = new FaceQuadInputs(TEXTURE_JOIN_ALL_TR,
                    BAKE_ROTATE_270, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TL_TR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TL_TR, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TR_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TL_TR, BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_BL_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TL_TR, BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TL_BL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TL_TR, BAKE_ROTATE_270, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TR_BL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TR_BL, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TL_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TR_BL, BAKE_ROTATE_90, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TR_BL_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TR_BL_BR, BAKE_ROTATE_NONE, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TL_BL_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TR_BL_BR, BAKE_ROTATE_90, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TL_TR_BL.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TR_BL_BR, BAKE_ROTATE_180, false, false);
            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TL_TR_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_TR_BL_BR, BAKE_ROTATE_270, false, false);

            FACE_INPUTS[face.ordinal()][CornerJoinFaceState.ALL_TL_TR_BL_BR.ordinal()] = new FaceQuadInputs(
                    TEXTURE_JOIN_ALL_ALL_CORNERS, BAKE_ROTATE_NONE, false, false);

            // rotate top face so they work - orientation is different from others
            if (face == Direction.UP) {
                for (int i = 0; i < FACE_INPUTS[face.ordinal()].length; i++) {
                    FaceQuadInputs fqi = FACE_INPUTS[face.ordinal()][i];
                    if (fqi != null && fqi != NO_BORDER) {
                        FACE_INPUTS[face.ordinal()][i] = new FaceQuadInputs(fqi.textureOffset, 
                                (fqi.rotation + 2) % 4, fqi.flipU, fqi.flipV);
                    }
                }
            } 
        }
    }

    public static boolean bakeBorderSprite(MutableQuadView quad, int spriteIndex, CornerJoinBlockState bjs) {
        Direction face = quad.nominalFace();
        CornerJoinFaceState cjfs = bjs.getFaceJoinState(face);
        FaceQuadInputs inputs = FACE_INPUTS[face.ordinal()][bjs.getFaceJoinState(face).ordinal()];
    
        // if can't identify a face, skip texturing
        // don't render the "no border" texture unless this is a tile of some kind
        if (inputs == null || inputs == NO_BORDER) {
            quad.sprite(0, spriteIndex, 0, 0)
                .sprite(1, spriteIndex, 0, 0)
                .sprite(2, spriteIndex, 0, 0)
                .sprite(3, spriteIndex, 0, 0);
            return false;
        }
        
        int bakeFlags = BAKE_NORMALIZED | BAKE_LOCK_UV | inputs.rotation;
        if(inputs.flipU) {
            bakeFlags |= BAKE_FLIP_U;
        }
        if(inputs.flipV ) {
            bakeFlags |= BAKE_FLIP_V;
        }
        
        //TODO: remove
//        if(face == Direction.UP)
//            System.out.println(bakeFlags);
        
        String textureName = "block/" + SmartChest.BORDER_TEX.textureName(0, inputs.textureOffset);
        Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas().getSprite(new Identifier(SmartChest.MODID, textureName));
//        quad.sprite(0, 1, 0, 0).sprite(1, 1, 0, 1).sprite(2, 1, 1, 1).sprite(3, 1, 1, 0);
        
        quad.spriteBake(spriteIndex, sprite, bakeFlags);
        return true;
    }
}
