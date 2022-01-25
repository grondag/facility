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

import grondag.facility.varia.WorldTaskManager;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.device.DeviceComponentAccess;
import grondag.fluidity.api.device.DeviceComponentType;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.wip.base.transport.BasicCarrier;
import grondag.fluidity.wip.base.transport.BasicCarrierSession;
import grondag.fluidity.wip.base.transport.BroadcastConsumer;
import grondag.fluidity.wip.base.transport.BroadcastSupplier;

public class UtbCarrierSession extends BasicCarrierSession<UtbCostFunction> {
	long lastTick = 0;
	boolean shouldTransmit = false;

	public UtbCarrierSession(BasicCarrier<UtbCostFunction> carrier, Function<DeviceComponentType<?>, DeviceComponentAccess<?>> componentFunction) {
		super(carrier, componentFunction);
	}

	@Override
	protected UtbBroadcastConsumer createBroadcastConsumer() {
		return new UtbBroadcastConsumer(this);
	}

	@Override
	protected UtbBroadcastSupplier createBroadcastSupplier() {
		return new UtbBroadcastSupplier(this);
	}

	boolean shouldTransmit() {
		final int thisTick = WorldTaskManager.tickCounter();

		if (thisTick > lastTick) {
			shouldTransmit = carrier.effectiveCarrier().costFunction().shouldTransmit();
			lastTick = thisTick;
		}

		return shouldTransmit;
	}

	protected class UtbBroadcastSupplier extends BroadcastSupplier<UtbCostFunction> {
		public UtbBroadcastSupplier(UtbCarrierSession fromNode) {
			super(fromNode);
		}

		@Override
		public long apply(Article item, long count, boolean simulate) {
			return shouldTransmit() ? super.apply(item, count, simulate) : 0;
		}

		@Override
		public Fraction apply(Article item, Fraction volume, boolean simulate) {
			return shouldTransmit() ? super.apply(item, volume, simulate) : Fraction.ZERO;
		}

		@Override
		public long apply(Article item, long numerator, long divisor, boolean simulate) {
			return shouldTransmit() ? super.apply(item, numerator, divisor, simulate) : 0;
		}
	}

	protected class UtbBroadcastConsumer extends BroadcastConsumer<UtbCostFunction> {
		public UtbBroadcastConsumer(UtbCarrierSession fromNode) {
			super(fromNode);
		}

		@Override
		public long apply(Article item, long count, boolean simulate) {
			return shouldTransmit() ? super.apply(item, count, simulate) : 0;
		}

		@Override
		public Fraction apply(Article item, Fraction volume, boolean simulate) {
			return shouldTransmit() ? super.apply(item, volume, simulate) : Fraction.ZERO;
		}

		@Override
		public long apply(Article item, long numerator, long divisor, boolean simulate) {
			return shouldTransmit() ? super.apply(item, numerator, divisor, simulate) : 0;
		}
	}
}
