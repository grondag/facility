package grondag.facility.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import grondag.facility.transport.UniversalTransportBus;
import grondag.facility.wip.transport.CarrierProvider;
import grondag.facility.wip.transport.SingleCarrierProvider;
import grondag.facility.wip.transport.impl.SubCarrier;
import grondag.fluidity.api.device.Authorization;
import grondag.fluidity.api.device.ComponentType;
import grondag.fluidity.api.device.Device;

public class PipeBlockEntity extends BlockEntity implements Device {
	SubCarrier carrier = new SubCarrier(UniversalTransportBus.BASIC);
	SingleCarrierProvider carrierProvider = SingleCarrierProvider.of(carrier);
	protected final PipeMultiBlock.Member member;
	protected PipeMultiBlock owner = null;

	public PipeBlockEntity(BlockEntityType<PipeBlockEntity> type) {
		super(type);
		member = new PipeMultiBlock.Member(this, b -> b.carrier);
	}

	@Override
	public <T> T getComponent(ComponentType<T> componentType, Authorization auth, Direction side, Identifier id) {
		return componentType == CarrierProvider.CARRIER_PROVIDER_COMPONENT ? componentType.cast(carrierProvider) : componentType.absent();
	}
}
