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

import grondag.facility.Facility;
import grondag.xm.api.primitive.SimplePrimitive;

public class PipeModel extends BasePipeModel {
	private static final PipeModel INSTANCE = new PipeModel();

	public static final SimplePrimitive PRIMITIVE = SimplePrimitive.builder()
			.surfaceList(SURFACES)
			.polyFactory(INSTANCE::polyFactory)
			.primitiveBitCount(PRIMITIVE_BIT_COUNT)
			.simpleJoin(true)
			.build(Facility.REG.id("basic_pipe"));

	protected PipeModel() {
		super(false);
	}

}
