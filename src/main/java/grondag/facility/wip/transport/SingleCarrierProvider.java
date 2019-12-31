package grondag.facility.wip.transport;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import grondag.facility.wip.transport.impl.CarrierImpl;
import grondag.fluidity.api.article.ArticleType;

public class SingleCarrierProvider implements CarrierProvider {
	protected final Carrier carrier;

	public SingleCarrierProvider(Carrier carrier) {
		this.carrier = carrier;
	}

	@Override
	public Carrier getCarrier(CarrierType type) {
		return type == carrier.carrierType() ? carrier : Carrier.EMPTY;
	}

	@Override
	public CarrierType getBestCarrier(ArticleType<?> type) {
		return carrier.carrierType().canCarry(type) ? carrier.carrierType() : CarrierType.EMPTY;
	}

	@Override
	public Set<CarrierType> carrierTypes() {
		return ImmutableSet.of(carrier.carrierType());
	}

	public static SingleCarrierProvider of(CarrierImpl carrier) {
		return new SingleCarrierProvider(carrier);
	}
}
