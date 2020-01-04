package grondag.facility.transport;

import java.util.function.Function;
import java.util.function.Supplier;

import grondag.fluidity.api.device.DeviceComponent;
import grondag.fluidity.api.device.DeviceComponentType;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.api.transport.CarrierType;
import grondag.fluidity.wip.base.transport.BasicCarrierSession;
import grondag.fluidity.wip.base.transport.SubCarrier;

public class UtbSubCarrier extends SubCarrier {
	protected final UtbCostFunction costFunction = new UtbCostFunction();
	protected final Supplier<ArticleFunction> costFunctionSupplier = () -> parentCarrier == null ? costFunction : ((UtbAggregateCarrier) parentCarrier).costFunction;

	public UtbSubCarrier(CarrierType carrierType) {
		super(carrierType);
	}

	@Override
	protected CarrierSession createSession(Function<DeviceComponentType<?>, DeviceComponent<?>> componentFunction) {
		return new BasicCarrierSession(this, componentFunction, costFunctionSupplier);
	}
}
