package grondag.facility.transport;

import grondag.fluidity.wip.api.transport.CarrierType;
import grondag.fluidity.wip.base.transport.AggregateCarrier;

public class UtbAggregateCarrier extends AggregateCarrier<UtbCostFunction> {
	protected final UtbCostFunction costFunction = new UtbCostFunction();

	public UtbAggregateCarrier(CarrierType carrierType) {
		super(carrierType);
	}

	@Override
	public UtbCostFunction costFunction() {
		return costFunction;
	}
}
