package grondag.facility.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import grondag.facility.block.ItemStorageBlockEntity.ItemStorageMultiblock;
import grondag.facility.block.PipeBlockEntity.PipeMultiblock;
import grondag.facility.wip.transport.CarrierDevice;
import grondag.facility.wip.transport.CarrierNode;
import grondag.facility.wip.transport.NodeDevice;
import grondag.facility.wip.transport.impl.CompoundCarrierDevice;
import grondag.fluidity.api.device.CompoundDeviceManager;
import grondag.fluidity.api.device.CompoundMemberDevice;
import grondag.fluidity.api.device.Device;

public class PipeBlockEntity extends AbstractFunctionalBlockEntity<NodeDevice> implements CarrierDevice, CompoundMemberDevice<PipeBlockEntity, PipeMultiblock> {

	protected static class PipeMultiblock extends CompoundCarrierDevice<PipeBlockEntity, PipeMultiblock> {

		@Override
		protected void onRemove(PipeBlockEntity device) {
			// TODO Auto-generated method stub

		}
	}

	protected static final CompoundDeviceManager<ItemStorageBlockEntity, ItemStorageMultiblock> DEVICE_MANAGER = CompoundDeviceManager.create(
			ItemStorageMultiblock::new, (ItemStorageBlockEntity a, ItemStorageBlockEntity b) -> ItemStorageBlock.canConnect(a, b));

	protected PipeMultiblock owner = null;

	public PipeBlockEntity(BlockEntityType<PipeBlockEntity> type) {
		super(type);
	}

	@Override
	protected void addNeighbor(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		if(!(be instanceof PipeBlockEntity) && be instanceof CarrierDevice) {
			neighbors.add((NodeDevice) be);

			((NodeDevice) be).onCarrierPresent(this);
		}
	}

	@Override
	public void onCarrierPresent(CarrierDevice carrierDevice) {
		if(carrierDevice.isGateway()) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	public CarrierNode attach(Device fromDevice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipeMultiblock getCompoundDevice() {
		return owner;
	}

	@Override
	public void setCompoundDevice(PipeMultiblock owner) {
		this.owner = owner;
	}
}
