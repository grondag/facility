package grondag.brocade.painting;


public class FaceQuadInputs {
    public final int textureOffset;
    public final int rotation;
    public final boolean flipU;
    public final boolean flipV;

    public FaceQuadInputs(int textureOffset, int rotation, boolean flipU, boolean flipV) {
        this.textureOffset = textureOffset;
        
        this.rotation = rotation;
        this.flipU = flipU;
        this.flipV = flipV;
    }
}