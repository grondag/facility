package grondag.facility.transport.model;

import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.EAST;
import static net.minecraft.util.math.Direction.NORTH;
import static net.minecraft.util.math.Direction.SOUTH;
import static net.minecraft.util.math.Direction.UP;
import static net.minecraft.util.math.Direction.WEST;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.mesh.Csg;
import grondag.xm.api.mesh.CsgMesh;
import grondag.xm.api.mesh.WritableMesh;
import grondag.xm.api.mesh.XmMesh;
import grondag.xm.api.mesh.XmMeshes;
import grondag.xm.api.mesh.polygon.MutablePolygon;
import grondag.xm.api.mesh.polygon.PolyTransform;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.paint.SurfaceTopology;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.surface.XmSurface;
import grondag.xm.api.primitive.surface.XmSurfaceList;
import grondag.xm.api.texture.content.XmTextures;

public class BasePipeModel {
	protected static final XmSurfaceList SURFACES = XmSurfaceList.builder()
			.add("side", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.add("end", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.add("connector", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.build();


	public static final XmSurface SURFACE_SIDE = SURFACES.get(0);
	public static final XmSurface SURFACE_END = SURFACES.get(1);
	public static final XmSurface SURFACE_CONNECTOR = SURFACES.get(2);

	public static final XmPaint PAINT_SIDE = XmPaint.finder()
			.textureDepth(1)
			.texture(0, XmTextures.TILE_NOISE_MODERATE)
			.textureColor(0, 0xFF707080)
			.find();

	public static final XmPaint PAINT_END = XmPaint.finder()
			.textureDepth(1)
			.texture(0, XmTextures.TILE_NOISE_SUBTLE)
			.textureColor(0, 0xFF303030)
			.find();

	public static final XmPaint PAINT_CONNECTOR = XmPaint.finder()
			.textureDepth(1)
			.texture(0, XmTextures.TILE_NOISE_SUBTLE)
			.textureColor(0, 0xFF404050)
			.find();

	// could be used to configure cable shape
	protected static final float THICKNESS = 6f / 16f;

	// these are derived
	protected static final float MIN = 0.5f - THICKNESS * 0.5f;
	protected static final float MAX = 1f - MIN;
	protected static final float END_MIN = Math.max(0, MIN - THICKNESS);
	protected static final float END_MAX = Math.min(1, MAX + THICKNESS);

	protected static final float CONNECTOR_DEPTH = 0.1f;
	protected static final float CONNECTOR_MARGIN = 0.1f;
	protected static final float CONNECTOR_MIN = Math.max(0, MIN - CONNECTOR_MARGIN);
	protected static final float CONNECTOR_MAX = Math.min(1, MAX + CONNECTOR_MARGIN);

	protected static final Direction[] FACES = Direction.values();

	protected final boolean alwaysConnects;

	protected BasePipeModel(boolean alwaysConnects) {
		this.alwaysConnects = alwaysConnects;
	}

	protected void emitSection(float from, float to, Axis axis, WritableMesh mesh) {
		final MutablePolygon writer = mesh.writer();
		final PolyTransform transform = PolyTransform.get(axis);
		writer.lockUV(0, true).surface(SURFACE_SIDE).saveDefaults();

		writer.setupFaceQuad(EAST, MIN, from, MAX, to, MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(WEST, MIN, from, MAX, to, MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(NORTH, MIN, from, MAX, to, MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(SOUTH, MIN, from, MAX, to, MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(UP, MIN, MIN, MAX, MAX, 1 - to, NORTH);
		writer.surface(to > MAX
				? SURFACE_END
						: SURFACE_SIDE);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(DOWN, MIN, MIN, MAX, MAX, from, NORTH);
		writer.surface(from < MIN
				? SURFACE_END
						: SURFACE_SIDE);
		transform.accept(writer);
		writer.append();
	}

	protected void emitConnector(PrimitiveState modelState, Direction face, WritableMesh mesh) {
		emitConnector(modelState, face, mesh, CONNECTOR_MIN, CONNECTOR_MAX, CONNECTOR_DEPTH);
	}

	protected void emitConnector(PrimitiveState modelState, Direction face, WritableMesh mesh, float connectorMin, float connectorMax, float connectorDepth) {
		final MutablePolygon writer = mesh.writer();
		final PolyTransform transform = PolyTransform.get(face);
		writer.lockUV(0, true).surface(SURFACE_CONNECTOR);
		writer.saveDefaults();

		writer.setupFaceQuad(EAST, connectorMin, 0, connectorMax, connectorDepth, connectorMin, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(WEST, connectorMin, 0, connectorMax, connectorDepth, connectorMin, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(NORTH, connectorMin, 0, connectorMax, connectorDepth, connectorMin, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(SOUTH, connectorMin, 0, connectorMax, connectorDepth, connectorMin, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(UP, connectorMin, connectorMin, connectorMax, connectorMax, 1 - connectorDepth, NORTH);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(DOWN, connectorMin, connectorMin, connectorMax, connectorMax, 0, NORTH);
		writer.surface(SURFACE_END);
		transform.accept(writer);
		writer.append();
	}

	protected boolean hasJoins(PrimitiveState modelState, SimpleJoinState joinState, Axis axis) {
		return joinState.hasJoins(axis);
	}

	protected boolean isJoined(PrimitiveState modelState, SimpleJoinState joinState, Direction face) {
		return joinState.isJoined(face);
	}

	protected boolean needsConnector(PrimitiveState modelState, int connectorBits, Direction face) {
		return (connectorBits & (1 << face.ordinal())) != 0;
	}

	protected XmMesh polyFactory(PrimitiveState modelState) {
		CsgMesh quadsA = null;
		CsgMesh quadsB = null;
		CsgMesh output = null;

		final SimpleJoinState joinState = modelState.simpleJoin();

		if(joinState == SimpleJoinState.NO_JOINS && !alwaysConnects) {
			quadsA = XmMeshes.claimCsg();
			quadsB = XmMeshes.claimCsg();
			output = XmMeshes.claimCsg();

			emitSection(END_MIN, END_MAX, Axis.X, quadsA);
			emitSection(END_MIN, END_MAX, Axis.Y, quadsB);
			Csg.union(quadsA, quadsB, output);
			quadsB.release();
			quadsB = output;
			output = XmMeshes.claimCsg();
			quadsA.clear();
			emitSection(END_MIN, END_MAX, Axis.Z, quadsA);
			Csg.union(quadsA, quadsB, output);
		} else {
			final boolean hasX = hasJoins(modelState, joinState, Axis.X);
			final boolean hasY = hasJoins(modelState, joinState, Axis.Y);
			final boolean hasZ = hasJoins(modelState, joinState, Axis.Z);
			final boolean hasMultiple = (hasX ? 1 : 0) + (hasY ? 1 : 0) + (hasZ ? 1 : 0)  > 1;

			if(hasX) {
				output = XmMeshes.claimCsg();
				final float top = isJoined(modelState, joinState, WEST) ? 1 : (hasMultiple ? MAX : END_MAX);
				final float bottom = isJoined(modelState, joinState, EAST) ? 0 : (hasMultiple ? MIN : END_MIN);
				emitSection(bottom, top, Axis.X, output);
			}

			if(hasY) {
				quadsA = XmMeshes.claimCsg();
				final float top = isJoined(modelState, joinState, UP) ? 1 : (hasMultiple ? MAX : END_MAX);
				final float bottom = isJoined(modelState, joinState, DOWN) ? 0 : (hasMultiple ? MIN : END_MIN);
				emitSection(bottom, top, Axis.Y, quadsA);

				if(output == null) {
					output = quadsA;
					quadsA = null;
				} else {
					quadsB = output;
					output = XmMeshes.claimCsg();
					Csg.union(quadsA, quadsB, output);
					quadsA.clear();
					quadsB.release();
					quadsB = null;
				}
			}

			if(hasZ) {
				if(quadsA == null) {
					quadsA = XmMeshes.claimCsg();
				}
				final float top = isJoined(modelState, joinState, SOUTH) ? 1 : (hasMultiple ? MAX : END_MAX);
				final float bottom = isJoined(modelState, joinState, NORTH) ? 0 : (hasMultiple ? MIN : END_MIN);
				emitSection(bottom, top, Axis.Z, quadsA);

				if(output == null) {
					output = quadsA;
					quadsA = null;
				} else {
					quadsB = output;
					output = XmMeshes.claimCsg();
					Csg.union(quadsA, quadsB, output);
					quadsA.clear();
					quadsB.release();
					quadsB = null;
				}
			}
		}

		final int connectorBits = modelState.primitiveBits();

		if(alwaysConnects || connectorBits != 0) {
			if(quadsA == null) {
				quadsA = XmMeshes.claimCsg();
			}

			for(final Direction face : FACES) {
				if(needsConnector(modelState, connectorBits, face)) {
					emitConnector(modelState, face, quadsA);
				}
			}
			quadsB = output;
			output = XmMeshes.claimCsg();
			Csg.union(quadsA, quadsB, output);
		}

		if(quadsA != null) {
			quadsA.release();
		}
		if(quadsB != null) {
			quadsB.release();
		}
		return output.releaseToReader();
	}
}
