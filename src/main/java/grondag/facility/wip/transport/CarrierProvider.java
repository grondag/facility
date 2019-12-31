package grondag.facility.wip.transport;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.ComponentType;
import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;

public interface CarrierProvider {
	@Nullable Carrier getCarrier(CarrierType type);

	CarrierType getBestCarrier(ArticleType<?> type);

	Set<CarrierType> carrierTypes();

	/**
	 *
	 * @param type
	 * @param fromNode
	 * @param broadcastConsumer
	 * @param broadcastSupplier
	 * @return  Will return existing connection if node is already connected.
	 */
	default CarrierSession attachIfPresent(CarrierType type, CarrierNode fromNode, Supplier<ArticleConsumer> nodeConsumerFactory, Supplier<ArticleSupplier> nodeSupplierFactory) {
		final Carrier carrier = getCarrier(type);
		return carrier == null || carrier == Carrier.EMPTY ? CarrierSession.INVALID : carrier.attach(fromNode, nodeConsumerFactory, nodeSupplierFactory);
	}

	default CarrierSession attachIfPresent(ArticleType<?> type, CarrierNode fromNode, Supplier<ArticleConsumer> nodeConsumerFactory, Supplier<ArticleSupplier> nodeSupplierFactory) {
		final CarrierType best = getBestCarrier(type);

		if(best == null || best == CarrierType.EMPTY) {
			return CarrierSession.INVALID;
		}

		return attachIfPresent(best, fromNode, nodeConsumerFactory, nodeSupplierFactory);
	}

	CarrierProvider EMPTY = new CarrierProvider() {
		@Override
		public Carrier getCarrier(CarrierType type) {
			return Carrier.EMPTY;
		}

		@Override
		public CarrierType getBestCarrier(ArticleType<?> type) {
			return CarrierType.EMPTY;
		}

		@Override
		public Set<CarrierType> carrierTypes() {
			return Collections.emptySet();
		}
	};

	ComponentType<CarrierProvider> CARRIER_PROVIDER_COMPONENT = () -> EMPTY;
}
