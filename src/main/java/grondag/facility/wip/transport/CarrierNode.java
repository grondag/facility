package grondag.facility.wip.transport;

import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;

public interface CarrierNode {
	long address();

	boolean isValid();

	ArticleConsumer broadcastConsumer();

	ArticleSupplier broadcastSupplier();

	StorageConnection connect(long remoteAddress);
}
