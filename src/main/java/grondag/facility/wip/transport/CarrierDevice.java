package grondag.facility.wip.transport;

import grondag.fluidity.api.device.Device;

/**
 * Device that exposes one or more carriers for external use.
 */
public interface CarrierDevice extends Device {
	default boolean isGateway() {
		return true;
	}
}
