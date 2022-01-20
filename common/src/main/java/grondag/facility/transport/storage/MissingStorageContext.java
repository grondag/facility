package grondag.facility.transport.storage;

import grondag.fluidity.api.storage.Store;

public class MissingStorageContext extends FluidityStorageContext {
	private MissingStorageContext() {
		store = Store.EMPTY;
	}

	public static final FluidityStorageContext INSTANCE = new MissingStorageContext();

	@Override
	protected Store store() {
		return Store.EMPTY;
	}

	@Override
	public boolean prepareForTick() {
		return false;
	}
}
