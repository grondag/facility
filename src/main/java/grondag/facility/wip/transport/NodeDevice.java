package grondag.facility.wip.transport;

import grondag.fluidity.api.device.Device;

/**
 * Device that exposes one or more carriers for external use.
 */
public interface NodeDevice extends Device {
	void onCarrierPresent(CarrierDevice carrierDevice);

	default boolean isCarrier() {
		return false;
	}
}