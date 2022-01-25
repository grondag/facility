/*
 * This file is part of Facility and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.facility.transport;

import java.util.function.Function;

import grondag.fluidity.api.device.DeviceComponentAccess;
import grondag.fluidity.api.device.DeviceComponentType;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.api.transport.CarrierType;
import grondag.fluidity.wip.base.transport.AggregateCarrier;
import grondag.fluidity.wip.base.transport.SubCarrier;

public class UtbSubCarrier extends SubCarrier<UtbCostFunction> {
	protected UtbCostFunction costFunction;

	public UtbSubCarrier(CarrierType carrierType) {
		super(carrierType);
	}

	@Override
	protected CarrierSession createSession(Function<DeviceComponentType<?>, DeviceComponentAccess<?>> componentFunction) {
		return new UtbCarrierSession(this, componentFunction);
	}

	@Override
	public UtbCostFunction costFunction() {
		UtbCostFunction result = costFunction;

		if (result == null) {
			result = new UtbCostFunction();
			costFunction = result;
		}

		return result;
	}

	@Override
	public void setParent(AggregateCarrier<UtbCostFunction> parent) {
		super.setParent(parent);
		costFunction = null;
	}
}
