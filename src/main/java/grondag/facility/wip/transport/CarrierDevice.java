package grondag.facility.wip.transport;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import grondag.fluidity.api.device.Device;
import grondag.fluidity.api.storage.Storage;

/**
 * Device that exposes one or more carriers for external use.
 */
public interface CarrierDevice extends NodeDevice {
	CarrierNode attach(Device fromDevice);

	default boolean isGateway() {
		return true;
	}

	@Override
	default Storage getStorage(Direction side, Identifier id) {
		return null;
	}

	@Override
	default boolean hasStorage(Direction side, Identifier id) {
		return false;
	}
}