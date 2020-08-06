package grondag.facility.transport.model;

import grondag.facility.varia.FacilityColors;
import grondag.xm.api.paint.VertexProcessor;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.api.texture.tech.TechTextures;

public abstract class PipePaints {
	private PipePaints() {}

	private static final int CABLE_BASE = 0xFF2E373E;
	private static final int CABLE_COLOR = FacilityColors.BASE[0];
	private static final int CABLE_HIGHLIGHT_COLOR =  FacilityColors.HIGHLIGHT[0];
	private static final int CONNECTOR_OUTSIDE_COLOR = 0xFF404050;
	private static final int CONNECTOR_FACE_COLOR = 0xFF303030;

	static final VertexProcessor HIGHLIGHT_COLOR = (poly, modelState, surface, paint, textureIndex) -> {
		if (BasePipeModel.hasGlow(modelState)) {
			poly.colorAll(textureIndex, FacilityColors.GLOW[modelState.species()]);
			poly.emissive(textureIndex, true);
		} else {
			poly.colorAll(textureIndex, FacilityColors.HIGHLIGHT[modelState.species()]);
		}
	};

	static final VertexProcessor BASE_COLOR = (poly, modelState, surface, paint, textureIndex) -> {
		poly.colorAll(textureIndex, FacilityColors.BASE[modelState.species()]);
	};

	public static final XmPaint CABLE = XmPaint.finder()
			.textureDepth(3)
			.texture(0, XmTextures.TILE_NOISE_SUBTLE)
			.textureColor(0, CABLE_BASE)

			.texture(1, TechTextures.CABLE_CENTER_4PX)
			.textureColor(1, CABLE_COLOR)
			.vertexProcessor(1, BASE_COLOR)

			.texture(2, TechTextures.CABLE_GLOWLINES_4PX)
			.textureColor(2, CABLE_HIGHLIGHT_COLOR)
			.vertexProcessor(2, HIGHLIGHT_COLOR)
			.find();

	public static final XmPaint STD_CONNECTOR_FACE = XmPaint.finder()
			.textureDepth(1)
			.texture(0, XmTextures.TILE_NOISE_SUBTLE)
			.textureColor(0, CONNECTOR_FACE_COLOR)
			.find();

	public static final XmPaint STD_CONNECTOR_BACK = XmPaint.finder()
			.textureDepth(1)
			.texture(0, XmTextures.TILE_NOISE_SUBTLE)
			.textureColor(0, CONNECTOR_OUTSIDE_COLOR)
			.find();

	public static final XmPaint STD_CONNECTOR_SIDE = XmPaint.finder().copy(STD_CONNECTOR_BACK).find();


	public static final XmPaint INPUT_CONNECTOR_FACE = XmPaint.finder().copy(STD_CONNECTOR_FACE)
			.textureDepth(2)
			.texture(1, TechTextures.CABLE_INPUT_DECAL)
			.vertexProcessor(1, HIGHLIGHT_COLOR)
			.textureColor(1, CABLE_HIGHLIGHT_COLOR)
			.find();

	public static final XmPaint OUTPUT_CONNECTOR_FACE = XmPaint.finder().copy(INPUT_CONNECTOR_FACE)
			.texture(1, TechTextures.CABLE_OUTPUT_DECAL).find();


	public static final XmPaint INPUT_CONNECTOR_BACK = XmPaint.finder().copy(STD_CONNECTOR_BACK)
			.textureDepth(2)
			.texture(1, TechTextures.CABLE_INPUT_DECAL)
			.vertexProcessor(1, HIGHLIGHT_COLOR)
			.textureColor(1, CABLE_HIGHLIGHT_COLOR)
			.find();

	public static final XmPaint OUTPUT_CONNECTOR_BACK = XmPaint.finder().copy(INPUT_CONNECTOR_BACK)
			.texture(1, TechTextures.CABLE_OUTPUT_DECAL).find();


	public static final XmPaint INPUT_CONNECTOR_SIDE = XmPaint.finder().copy(STD_CONNECTOR_SIDE)
			.textureDepth(2)
			.texture(1, TechTextures.CABLE_INPUT_ARROWS)
			.vertexProcessor(1, HIGHLIGHT_COLOR)
			.textureColor(1, CABLE_HIGHLIGHT_COLOR)
			.find();

	public static final XmPaint OUTPUT_CONNECTOR_SIDE = XmPaint.finder().copy(INPUT_CONNECTOR_SIDE)
			.texture(1, TechTextures.CABLE_OUTPUT_ARROWS).find();

}
