package grondag.facility.wip.transport.impl;

import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import grondag.facility.wip.transport.Carrier;
import grondag.facility.wip.transport.CarrierListener;
import grondag.facility.wip.transport.CarrierNode;
import grondag.facility.wip.transport.CarrierSession;
import grondag.facility.wip.transport.CarrierType;
import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;
import grondag.fluidity.base.storage.component.ListenerSet;

public class CarrierImpl implements Carrier {
	protected final CarrierType carrierType;

	public CarrierImpl(CarrierType carrierType) {
		this.carrierType = carrierType;
	}

	protected final ListenerSet<CarrierListener> listeners = new ListenerSet<>(this::sendFirstListenerUpdate, this::sendLastListenerUpdate, this::onListenersEmpty);

	protected final Long2ObjectOpenHashMap<CarrierSession> nodes = new Long2ObjectOpenHashMap<>();

	protected void sendFirstListenerUpdate(CarrierListener listener) {
		nodes.values().forEach(a -> listener.onAttach(CarrierImpl.this, a));
	}

	protected void sendLastListenerUpdate(CarrierListener listener) {
		nodes.values().forEach(a -> listener.onDetach(CarrierImpl.this, a));
	}

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
	public CarrierSession attach(CarrierNode fromNode, Supplier<ArticleConsumer> nodeConsumerFactor, Supplier<ArticleSupplier> nodeSupplierFactory) {
		final CarrierSessionImpl result = new CarrierSessionImpl(this, nodeConsumerFactor, nodeSupplierFactory);

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

	public Carrier effectiveCarrier() {
		return this;
	}
}
