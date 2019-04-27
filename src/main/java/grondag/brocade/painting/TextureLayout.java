package grondag.brocade.painting;


public enum TextureLayout {
    /**
     * Separate random tiles with naming convention base_j_i where i is 0-7 and j is
     * 0 or more.
     */
    SPLIT_X_8(0),

    /**
     * Single square file with optional versions. If more than one version, file
     * names should have a 0-based _x suffix.
     */
    SIMPLE(0),

    /**
     * Separate files with naming convention same as SPLIT_X_8 except only the start
     * 13 textures out of every 16 are used for borders. Texture 14 contains the
     * face that should be rendered if the border is rendered in the solid render
     * layer. It is IMPORTANT that texture 14 have a solid alpha channel - otherwise
     * mipmap generation will be borked. The solid face won't be used at all if
     * rendering in a non-solid layer. Files won't exist or will be blank for 14 and
     * 15.
     */
    BORDER_13(0, 14),

    /**
     * Separate files with naming convention same as SPLIT_X_8 except only the start
     * 5 textures out of every 8. Files won't exist or will be blank for 5-7.
     */
    MASONRY_5(0, 5),

    /**
     * Animated big textures stored as series of .jpg files
     */
    BIGTEX_ANIMATED(0),

    /**
     * Compact border texture on format, typically with multiple variants. Each
     * quadrant of the texture represents one quadrant of a face that can be
     * connected. All are present on same image. Each quadrant must be able to
     * connect with other quadrants in any (connecting) rotation or texture
     * variation.
     * <p>
     * 
     * Follows same naming convention as {@link #SIMPLE}.
     */
    QUADRANT_CONNECTED(0);

    private TextureLayout(int stateFlags) {
        this(stateFlags, 1);
    }

    private TextureLayout(int stateFlags, int textureCount) {
        this.modelStateFlag = stateFlags;
        this.textureCount = textureCount;
        this.blockRowCount = (textureCount + 7) / 8;
        this.blockTextureCount = blockRowCount * 8;
    }

    /**
     * identifies the world state needed to drive texture random rotation/selection
     */
    public final int modelStateFlag;

    /**
     * Textures per variant in this layout.
     */
    public final int textureCount;

    /**
     * If the texture is arranged as blocks with primary and secondary numbers, <br>
     * The number of distinct primary values per variant.
     * <p>
     * 
     * Equivalently if the first number is row, the second number is column, and
     * this is the number of rows.
     * 
     */
    public final int blockRowCount;

    /**
     * Count of texture positions per variant, assuming 8-column rows, is simply
     * {@link #blockRowCount} * 8.
     */
    public final int blockTextureCount;
}
