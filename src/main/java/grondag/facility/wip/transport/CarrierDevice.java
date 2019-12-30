package grondag.facility.wip.transport;

import javax.annotation.Nullable;

import net.minecraft.util.math.Direction;

import grondag.fluidity.api.device.Device;
import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;

/**
 * Device that exposes one or more carriers for external use.
 */
public interface CarrierDevice extends NodeDevice {
	CarrierNode attach(Device fromDevice, @Nullable Direction fromSide, @Nullable ArticleConsumer broadcastConsumer, @Nullable ArticleSupplier broadcastSupplier);

	default CarrierNode attach(Device fromDevice, @Nullable Direction fromSide) {
		return attach(fromDevice, fromSide, null, null);
	}

	default boolean isGateway() {
		return true;
	}
}