package grondag.facility.transport.storage;

import grondag.fluidity.api.storage.Store;

public class MissingStorageContext extends FluidityStorageContext {
	private MissingStorageContext() {}

	public static final FluidityStorageContext INSTANCE = new MissingStorageContext();

	@Override
	protected Store store() {
		return Store.EMPTY;
	}

	@Override
	public void prepareForTick() {
		// NOOP
	}
}
