package grondag.facility.wip.transport;

import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;

/**
 * Visible to other nodes
 *
 */
public interface CarrierEndpoint {
	Carrier carrier();

	long address();

	boolean isValid();

	ArticleConsumer nodeConsumer();

	ArticleSupplier nodeSupplier();

	CarrierEndpoint INVALID = CarrierSession.INVALID;
}
