/*******************************************************************************
 * Copyright 2019, 2020 grondag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.facility.transport;

import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.EAST;
import static net.minecraft.util.math.Direction.NORTH;
import static net.minecraft.util.math.Direction.SOUTH;
import static net.minecraft.util.math.Direction.UP;
import static net.minecraft.util.math.Direction.WEST;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

import grondag.facility.Facility;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.mesh.Csg;
import grondag.xm.api.mesh.CsgMesh;
import grondag.xm.api.mesh.WritableMesh;
import grondag.xm.api.mesh.XmMesh;
import grondag.xm.api.mesh.XmMeshes;
import grondag.xm.api.mesh.polygon.MutablePolygon;
import grondag.xm.api.mesh.polygon.PolyTransform;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.paint.SurfaceTopology;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.SimplePrimitive;
import grondag.xm.api.primitive.surface.XmSurface;
import grondag.xm.api.primitive.surface.XmSurfaceList;
import grondag.xm.api.texture.XmTextures;

public class PipeModel {
	static final XmSurfaceList SURFACES = XmSurfaceList.builder()
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
	private static final float THICKNESS = 6f / 16f;
	private static final float CONNECTOR_DEPTH = 0.1f;
	private static final float CONNECTOR_MARGIN = 0.1f;

	// these are derived
	private static final float MIN = 0.5f - THICKNESS * 0.5f;
	private static final float MAX = 1f - MIN;
	private static final float END_MIN = Math.max(0, MIN - THICKNESS);
	private static final float END_MAX = Math.min(1, MAX + THICKNESS);
	private static final float CONNECTOR_MIN = Math.max(0, MIN - CONNECTOR_MARGIN);
	private static final float CONNECTOR_MAX = Math.min(1, MAX + CONNECTOR_MARGIN);

	static final Direction[] FACES = Direction.values();

	private static final void emitSection(float from, float to, Axis axis, WritableMesh mesh) {
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

	private static final void emitConnector(Direction face, WritableMesh mesh) {
		final MutablePolygon writer = mesh.writer();
		final PolyTransform transform = PolyTransform.get(face);
		writer.lockUV(0, true).surface(SURFACE_CONNECTOR);
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
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(DOWN, CONNECTOR_MIN, CONNECTOR_MIN, CONNECTOR_MAX, CONNECTOR_MAX, 0, NORTH);
		writer.surface(SURFACE_END);
		transform.accept(writer);
		writer.append();
	}

	private static XmMesh polyFactory(PrimitiveState modelState) {
		CsgMesh quadsA = null;
		CsgMesh quadsB = null;
		CsgMesh output = null;


		final SimpleJoinState state = modelState.simpleJoin();

		if(state == SimpleJoinState.NO_JOINS) {
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
			final boolean hasX = state.hasJoins(Axis.X);
			final boolean hasY = state.hasJoins(Axis.Y);
			final boolean hasZ = state.hasJoins(Axis.Z);
			final boolean hasMultiple = (hasX ? 1 : 0) + (hasY ? 1 : 0) + (hasZ ? 1 : 0)  > 1;

			if(hasX) {
				output = XmMeshes.claimCsg();
				final float top = state.isJoined(WEST) ? 1 : (hasMultiple ? MAX : END_MAX);
				final float bottom = state.isJoined(EAST) ? 0 : (hasMultiple ? MIN : END_MIN);
				emitSection(bottom, top, Axis.X, output);
			}

			if(hasY) {
				quadsA = XmMeshes.claimCsg();
				final float top = state.isJoined(UP) ? 1 : (hasMultiple ? MAX : END_MAX);
				final float bottom = state.isJoined(DOWN) ? 0 : (hasMultiple ? MIN : END_MIN);
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
				final float top = state.isJoined(SOUTH) ? 1 : (hasMultiple ? MAX : END_MAX);
				final float bottom = state.isJoined(NORTH) ? 0 : (hasMultiple ? MIN : END_MIN);
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
		if(connectorBits != 0) {
			if(quadsA == null) {
				quadsA = XmMeshes.claimCsg();
			}
			for(final Direction face : FACES) {
				if((connectorBits & (1 << face.ordinal())) != 0) {
					emitConnector(face, quadsA);
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

	public static final SimplePrimitive PRIMITIVE = SimplePrimitive.builder()
			.surfaceList(SURFACES)
			.polyFactory(PipeModel::polyFactory)
			.primitiveBitCount(6)
			.simpleJoin(true)
			.build(Facility.REG.id("basic_pipe"));

	public static final PrimitiveStateMutator MODEL_STATE_UPDATE = (modelState, xmBlockState, world, pos, neighbors, refreshFromWorld) -> {
		// join should already be handled, so we just need to check if neighbors are inventory
		if(refreshFromWorld) {
			int bits = 0;
			final SimpleJoinState join = modelState.simpleJoin();

			for(final Direction face : FACES) {
				if(join.isJoined(face)) {
					// TODO: make this more generic or move to registration
					if(!(neighbors.blockEntity(face) instanceof PipeBlockEntity)) {
						bits |= 1 << face.ordinal();
					};
				}
			}

			modelState.primitiveBits(bits);
		}
	};
}
