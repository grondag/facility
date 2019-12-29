package grondag.facility.wip.transport;

import javax.annotation.Nullable;

import grondag.fluidity.api.storage.Storage;

public interface StorageConnection {
	boolean isOpen();

	@Nullable Storage getStorage();

	long remoteAddress();

	void close();
}
