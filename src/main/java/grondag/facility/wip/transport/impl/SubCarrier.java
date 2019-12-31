package grondag.facility.wip.transport.impl;

import java.util.function.Supplier;

import grondag.facility.wip.transport.Carrier;
import grondag.facility.wip.transport.CarrierListener;
import grondag.facility.wip.transport.CarrierNode;
import grondag.facility.wip.transport.CarrierSession;
import grondag.facility.wip.transport.CarrierType;
import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;

public class SubCarrier extends CarrierImpl {
	protected AggregateCarrier parentCarrier = null;

	public SubCarrier(CarrierType carrierType) {
		super(carrierType);
	}

	public void setParent(AggregateCarrier parent) {
		parentCarrier = parent;
	}

	public AggregateCarrier getParent() {
		return parentCarrier;
	}

	@Override
	public Carrier effectiveCarrier() {
		return parentCarrier == null ? this : parentCarrier;
	}

	@Override
	protected void sendFirstListenerUpdate(CarrierListener listener) {
		nodes.values().forEach(a -> listener.onAttach(SubCarrier.this, a));
	}

	@Override
	protected void sendLastListenerUpdate(CarrierListener listener) {
		nodes.values().forEach(a -> listener.onDetach(SubCarrier.this, a));
	}

	@Override
	protected void onListenersEmpty() {
		// NOOP
	}

	@Override
	public CarrierType carrierType() {
		return carrierType;
	}

	@Override
	public void startListening(CarrierListener listener, boolean sendNotifications) {
		listeners.startListening(listener, sendNotifications);
	}

	@Override
	public void stopListening(CarrierListener listener, boolean sendNotifications) {
		listeners.stopListening(listener, sendNotifications);
	}

	@Override
	public CarrierSession attach(CarrierNode fromNode, Supplier<ArticleConsumer> nodeConsumerFactory, Supplier<ArticleSupplier> nodeSupplierFactory) {
		final CarrierSessionImpl result = new CarrierSessionImpl(this, nodeConsumerFactory, nodeSupplierFactory);

		if(nodes.put(result.address, result) == null) {
			listeners.forEach(l -> l.onAttach(this, result));
		}

		return result;
	}

	@Override
	public void detach(CarrierSession node) {
		if(nodes.remove(node.address()) != null) {
			listeners.forEach(l -> l.onDetach(this, node));
		}
	}

	@Override
	public int nodeCount() {
		return nodes.size();
	}

	@Override
	public Iterable<? extends CarrierSession> nodes() {
		return nodes.values();
	}
}
