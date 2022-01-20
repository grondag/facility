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

		if(result == null) {
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
