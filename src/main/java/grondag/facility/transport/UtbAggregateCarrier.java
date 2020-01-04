package grondag.facility.transport;

import grondag.fluidity.wip.api.transport.CarrierType;
import grondag.fluidity.wip.base.transport.AggregateCarrier;

public class UtbAggregateCarrier extends AggregateCarrier {
	protected final UtbCostFunction costFunction = new UtbCostFunction();

	public UtbAggregateCarrier(CarrierType carrierType) {
		super(carrierType);
	}
}
