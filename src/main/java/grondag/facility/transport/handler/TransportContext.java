package grondag.facility.transport.handler;

import grondag.facility.transport.buffer.TransportBuffer;
import grondag.facility.transport.storage.TransportStorageContext;

public interface TransportContext {
	TransportBuffer buffer();

	TransportStorageContext storageContext();

	default boolean isValid() {
		return storageContext().isValid();
	}

	TransportCarrierContext carrierContext();
}
