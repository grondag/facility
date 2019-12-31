package grondag.facility.wip.transport;

import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;

/**
 * Visible to the node that obtained the carrier.
 *
 */
public interface CarrierSession extends CarrierEndpoint {
	ArticleConsumer broadcastConsumer();

	ArticleSupplier broadcastSupplier();

	StorageConnection connect(long remoteAddress);

	void close();


	CarrierSession INVALID = new CarrierSession() {

		@Override
		public Carrier carrier() {
			return Carrier.EMPTY;
		}

		@Override
		public long address() {
			return -1;
		}

		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public ArticleConsumer broadcastConsumer() {
			return ArticleConsumer.FULL;
		}

		@Override
		public ArticleSupplier broadcastSupplier() {
			return ArticleSupplier.EMPTY;
		}

		@Override
		public StorageConnection connect(long remoteAddress) {
			return null;
		}

		@Override
		public void close() {
			// NOOP
		}

		@Override
		public ArticleConsumer nodeConsumer() {
			return ArticleConsumer.FULL;
		}

		@Override
		public ArticleSupplier nodeSupplier() {
			return ArticleSupplier.EMPTY;
		}
	};
}
