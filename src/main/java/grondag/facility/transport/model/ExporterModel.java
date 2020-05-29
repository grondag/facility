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
package grondag.facility.transport.model;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

import grondag.facility.Facility;
import grondag.fermion.orientation.api.OrientationType;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.mesh.WritableMesh;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.primitive.SimplePrimitive;

public class ExporterModel extends BasePipeModel {
	private static final ExporterModel INSTANCE = new ExporterModel();

	protected static final float EXT_CONNECTOR_DEPTH = 0.75f;
	protected static final float EXT_CONNECTOR_MARGIN = 0.12f;
	protected static final float EXT_CONNECTOR_MIN = Math.max(0, MIN - EXT_CONNECTOR_MARGIN);
	protected static final float EXT_CONNECTOR_MAX = Math.min(1, MAX + EXT_CONNECTOR_MARGIN);

	protected ExporterModel() {
		super(true);
	}

	@Override
	protected boolean hasJoins(PrimitiveState modelState, SimpleJoinState joinState, Axis axis) {
		return FACES[modelState.orientationIndex()].getAxis() == axis || super.hasJoins(modelState, joinState, axis);
	}

	@Override
	protected boolean isJoined(PrimitiveState modelState, SimpleJoinState joinState, Direction face) {
		return modelState.orientationIndex() == face.ordinal() || super.isJoined(modelState, joinState, face);
	}

	@Override
	protected boolean needsConnector(PrimitiveState modelState, int connectorBits, Direction face) {
		return modelState.orientationIndex() == face.ordinal() || super.needsConnector(modelState, connectorBits, face);
	}

	@Override
	protected void emitConnector(PrimitiveState modelState, Direction face, WritableMesh mesh) {
		if(modelState.orientationIndex() == face.ordinal()) {
			emitConnector(modelState, face, mesh, EXT_CONNECTOR_MIN, EXT_CONNECTOR_MAX, EXT_CONNECTOR_DEPTH);
		} else {
			super.emitConnector(modelState, face, mesh);
		}
	}

	public static final SimplePrimitive PRIMITIVE = SimplePrimitive.builder()
			.surfaceList(SURFACES)
			.polyFactory(INSTANCE::polyFactory)
			.orientationType(OrientationType.FACE)
			.primitiveBitCount(6)
			.simpleJoin(true)
			.build(Facility.REG.id("export_pipe"));

}
