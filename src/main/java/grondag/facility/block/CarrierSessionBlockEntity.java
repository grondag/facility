package grondag.facility.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import grondag.facility.wip.transport.CarrierNode;
import grondag.facility.wip.transport.CarrierSession;
import grondag.fluidity.api.device.Authorization;
import grondag.fluidity.api.device.ComponentType;
import grondag.fluidity.api.device.Device;

public abstract class CarrierSessionBlockEntity extends NeighboredBlockEntity<CarrierSession> implements Device, CarrierNode {
	public CarrierSessionBlockEntity(BlockEntityType<? extends CarrierSessionBlockEntity> type) {
		super(type);
	}

	protected abstract CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide);

	@Override
	protected void addNeighbor(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		final CarrierSession session = getSession(be, neighborPos, neighborSide);
		// TODO: remove or make this proper tracing
		//		System.out.println(this.getClass().getCanonicalName() + " @" + pos.toString() + " assigned address " + session.address()
		//		+ (session.isValid() ? " (valid)" : " (INVALID)" + " with carrier " + be.getClass().getCanonicalName()));

		if(session != null && session.isValid()) {
			neighbors.add(session);
		}
	}

	protected abstract <T> T getOtherComponent(ComponentType<T> serviceType, Authorization auth, Direction side, Identifier id);

	@Override
	public <T> T getComponent(ComponentType<T> serviceType, Authorization auth, Direction side, Identifier id) {
		if (serviceType == CarrierNode.CARRIER_NODE_COMPONENT) {
			return serviceType.cast(this);
		} else {
			return getOtherComponent(serviceType, auth, side, id);
		}
	}

	protected static final BlockPos.Mutable searchPos = new BlockPos.Mutable();

	protected boolean isReceivingRedstonePower() {
		final BlockPos pos = this.pos;
		final World world = this.world;

		if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.DOWN), Direction.UP) > 0) {
			return true;
		} else if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.UP), Direction.DOWN) > 0) {
			return true;
		} else if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.NORTH), Direction.SOUTH) > 0) {
			return true;
		} else if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.SOUTH), Direction.NORTH) > 0) {
			return true;
		} else if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.WEST), Direction.EAST) > 0) {
			return true;
		} else {
			return world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.EAST), Direction.WEST) > 0;
		}
	}
}
