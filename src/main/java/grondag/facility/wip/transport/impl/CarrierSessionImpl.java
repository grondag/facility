package grondag.facility.wip.transport.impl;

import java.util.function.Consumer;
import java.util.function.Supplier;

import grondag.facility.wip.transport.Carrier;
import grondag.facility.wip.transport.CarrierSession;
import grondag.facility.wip.transport.StorageConnection;
import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;
import grondag.fluidity.api.transact.TransactionContext;
import grondag.fluidity.api.transact.TransactionParticipant.TransactionDelegate;

class CarrierSessionImpl implements CarrierSession, TransactionDelegate {
	final long address = AssignedNumbersAuthority.assign();
	final Supplier<ArticleConsumer> nodeConsumerFactory;
	final Supplier<ArticleSupplier> nodeSupplierFactory;
	final CarrierImpl carrier;
	protected boolean isOpen = true;

	CarrierSessionImpl(CarrierImpl carrier, Supplier<ArticleConsumer> nodeConsumerFactory, Supplier<ArticleSupplier> nodeSupplierFactory) {
		this.carrier = carrier;
		this.nodeConsumerFactory = nodeConsumerFactory;
		this.nodeSupplierFactory = nodeSupplierFactory;
	}

	@Override
	public long address() {
		return address;
	}

	@Override
	public boolean isValid() {
		return isOpen;
	}

	protected final ArticleConsumer broadcastConsumer = new BroadcastConsumer(this);

	@Override
	public ArticleConsumer broadcastConsumer() {
		return broadcastConsumer;
	}

	protected final ArticleSupplier broadcastSupploer = new BroadcastSupplier(this);

	@Override
	public ArticleSupplier broadcastSupplier() {
		return broadcastSupploer;
	}

	@Override
	public StorageConnection connect(long remoteAddress) {
		return null;
	}

	@Override
	public void close() {
		if(isOpen) {
			isOpen = false;
			carrier.detach(this);
		}
	}

	@Override
	public Consumer<TransactionContext> prepareRollback(TransactionContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Carrier carrier() {
		return carrier.effectiveCarrier();
	}

	@Override
	public ArticleConsumer nodeConsumer() {
		return nodeConsumerFactory.get();
	}

	@Override
	public ArticleSupplier nodeSupplier() {
		return nodeSupplierFactory.get();
	}
}