package grondag.facility.transport.model;

import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.EAST;
import static net.minecraft.util.math.Direction.NORTH;
import static net.minecraft.util.math.Direction.SOUTH;
import static net.minecraft.util.math.Direction.UP;
import static net.minecraft.util.math.Direction.WEST;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

import grondag.facility.transport.PipeBlockEntity;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.mesh.Csg;
import grondag.xm.api.mesh.CsgMesh;
import grondag.xm.api.mesh.WritableMesh;
import grondag.xm.api.mesh.XmMesh;
import grondag.xm.api.mesh.XmMeshes;
import grondag.xm.api.mesh.polygon.MutablePolygon;
import grondag.xm.api.mesh.polygon.PolyTransform;
import grondag.xm.api.modelstate.base.BaseModelState;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.paint.SurfaceTopology;
import grondag.xm.api.primitive.surface.XmSurface;
import grondag.xm.api.primitive.surface.XmSurfaceList;

public class BasePipeModel {
	protected static final XmSurfaceList SURFACES = XmSurfaceList.builder()
			.add("cable", SurfaceTopology.CUBIC, XmSurface.FLAG_ALLOW_BORDERS)
			.add("connector_face", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.add("connector_side", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.add("connector_back", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.build();

	public static final XmSurface SURFACE_CABLE = SURFACES.get(0);
	public static final XmSurface SURFACE_CONNECTOR_FACE = SURFACES.get(1);
	public static final XmSurface SURFACE_CONNECTOR_SIDE = SURFACES.get(2);
	public static final XmSurface SURFACE_CONNECTOR_BACK = SURFACES.get(3);

	protected static final float CABLE_THICKNESS = 6f / 16f;

	protected static final float CABLE_MIN = 0.5f - CABLE_THICKNESS * 0.5f;
	protected static final float CABLE_MAX = 1f - CABLE_MIN;
	protected static final float END_MIN = Math.max(0, CABLE_MIN - CABLE_THICKNESS);
	protected static final float END_MAX = Math.min(1, CABLE_MAX + CABLE_THICKNESS);

	protected static final float CONNECTOR_DEPTH = 1f / 16f;
	protected static final float CONNECTOR_WIDTH = 8f / 16f;
	protected static final float CONNECTOR_MIN = 0.5f - CONNECTOR_WIDTH * 0.5f;
	protected static final float CONNECTOR_MAX = 1f - CONNECTOR_MIN;

	protected static final Direction[] FACES = Direction.values();

	/** six for connectors, 1 for emissive */
	protected static final int PRIMITIVE_BIT_COUNT = 7;

	protected static final int GLOW_BIT = 1 << 6;

	public static boolean hasGlow(BaseModelState<?,?> modelState) {
		return (modelState.primitiveBits() & GLOW_BIT) == GLOW_BIT;
	}

	public static final PrimitiveStateMutator GLOW_UPDATE = (modelState, xmBlockState, world, pos, neighbors, refreshFromWorld) -> {
		if(refreshFromWorld) {
			int bits = 0;
			final SimpleJoinState join = modelState.simpleJoin();

			for(final Direction face : FACES) {
				if(join.isJoined(face)) {
					if(!(neighbors.blockEntity(face) instanceof PipeBlockEntity)) {
						bits |= 1 << face.ordinal();
					};
				}
			}

			modelState.primitiveBits(bits);
		}
	};

	protected final boolean alwaysConnects;

	protected BasePipeModel(boolean alwaysConnects) {
		this.alwaysConnects = alwaysConnects;
	}

	protected void emitSection(float from, float to, Axis axis, WritableMesh mesh) {
		final MutablePolygon writer = mesh.writer();
		final PolyTransform transform = PolyTransform.get(axis);
		writer.lockUV(0, true).surface(SURFACE_CABLE).saveDefaults();

		writer.setupFaceQuad(EAST, CABLE_MIN, from, CABLE_MAX, to, CABLE_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(WEST, CABLE_MIN, from, CABLE_MAX, to, CABLE_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(NORTH, CABLE_MIN, from, CABLE_MAX, to, CABLE_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(SOUTH, CABLE_MIN, from, CABLE_MAX, to, CABLE_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(UP, CABLE_MIN, CABLE_MIN, CABLE_MAX, CABLE_MAX, 1 - to, NORTH);
		writer.surface(to > CABLE_MAX
				? SURFACE_CONNECTOR_FACE
						: SURFACE_CABLE);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(DOWN, CABLE_MIN, CABLE_MIN, CABLE_MAX, CABLE_MAX, from, NORTH);
		writer.surface(from < CABLE_MIN
				? SURFACE_CONNECTOR_FACE
						: SURFACE_CABLE);
		transform.accept(writer);
		writer.append();
	}

	protected void emitConnector(PrimitiveState modelState, Direction face, WritableMesh mesh) {
		final MutablePolygon writer = mesh.writer();
		final PolyTransform transform = PolyTransform.get(face);
		writer.lockUV(0, true)
		.surface(SURFACE_CONNECTOR_SIDE);
		writer.saveDefaults();

		writer.setupFaceQuad(EAST, CONNECTOR_MIN, 0, CONNECTOR_MAX, CONNECTOR_DEPTH, CONNECTOR_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(WEST, CONNECTOR_MIN, 0, CONNECTOR_MAX, CONNECTOR_DEPTH, CONNECTOR_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(NORTH, CONNECTOR_MIN, 0, CONNECTOR_MAX, CONNECTOR_DEPTH, CONNECTOR_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(SOUTH, CONNECTOR_MIN, 0, CONNECTOR_MAX, CONNECTOR_DEPTH, CONNECTOR_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(UP, CONNECTOR_MIN, CONNECTOR_MIN, CONNECTOR_MAX, CONNECTOR_MAX, 1 - CONNECTOR_DEPTH, NORTH);
		writer.surface(SURFACE_CONNECTOR_BACK);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(DOWN, CONNECTOR_MIN, CONNECTOR_MIN, CONNECTOR_MAX, CONNECTOR_MAX, 0, NORTH);
		writer.surface(SURFACE_CONNECTOR_FACE);
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
				final float top = isJoined(modelState, joinState, WEST) ? 1 : (hasMultiple ? CABLE_MAX : END_MAX);
				final float bottom = isJoined(modelState, joinState, EAST) ? 0 : (hasMultiple ? CABLE_MIN : END_MIN);
				emitSection(bottom, top, Axis.X, output);
			}

			if(hasY) {
				quadsA = XmMeshes.claimCsg();
				final float top = isJoined(modelState, joinState, UP) ? 1 : (hasMultiple ? CABLE_MAX : END_MAX);
				final float bottom = isJoined(modelState, joinState, DOWN) ? 0 : (hasMultiple ? CABLE_MIN : END_MIN);
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
				final float top = isJoined(modelState, joinState, SOUTH) ? 1 : (hasMultiple ? CABLE_MAX : END_MAX);
				final float bottom = isJoined(modelState, joinState, NORTH) ? 0 : (hasMultiple ? CABLE_MIN : END_MIN);
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
