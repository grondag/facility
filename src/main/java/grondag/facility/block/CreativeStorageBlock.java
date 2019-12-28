package grondag.facility.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CreativeStorageBlock extends Block implements BlockEntityProvider {
	protected final Supplier<BlockEntity> beFactory;

	public CreativeStorageBlock(Block.Settings settings, Supplier<BlockEntity> beFactory) {
		super(settings);
		this.beFactory = beFactory;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return beFactory.get();
	}

	@Override
	public boolean hasBlockEntity() {
		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		updateBe(iWorld, blockPos);
		return blockState;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		updateBe(world, blockPos);
		super.onBlockAdded(blockState, world, blockPos, blockState2, bl);
	}

	protected void updateBe(IWorld world, BlockPos pos) {
		final BlockEntity be = world.getBlockEntity(pos);

		if(be instanceof CreativeBlockEntity) {
			((CreativeBlockEntity) be).updateNeighbors();
		}
	}


	//	protected static class TracerAccess extends Item {
	//		protected static HitResult trace(World world, PlayerEntity player) {
	//			return rayTrace(world, player, RayTraceContext.FluidHandling.NONE);
	//		}
	//
	//		protected TracerAccess() {
	//			super(new Settings());
	//		}
	//	}

	//	protected static long lastClickMs = 0;
	//
	//	@Override
	//	public boolean onAttackInteraction(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction face) {
	//		if(world.isClient && state.getBlock() == this) {
	//			final long t = System.currentTimeMillis();
	//			final long d = t - lastClickMs;
	//			lastClickMs = t;
	//
	//			if(d > 100 && face.getOpposite() == state.get(XmProperties.FACE)) {
	//				BinActionC2S.send(pos, getHitHandle(world, player, face), true);
	//			}
	//		}
	//
	//		return false;
	//	}

	//	protected int getHitHandle(World world, PlayerEntity player, Direction face) {
	//		return divisionLevel > 1 ? getHitHandle(TracerAccess.trace(world, player), face) : 0;
	//	}
	//
	//	protected int getHitHandle(HitResult hit, Direction face) {
	//		if(divisionLevel > 1) {
	//			if(hit != null && hit.getType() == HitResult.Type.BLOCK)  {
	//				final Vec3d vec = hit.getPos();
	//				final Pair<Direction, Direction> faces = WorldHelper.closestAdjacentFaces(face, vec.x, vec.y, vec.z);
	//				final FaceEdge e1 = FaceEdge.fromWorld(faces.getLeft(), face);
	//				final FaceEdge e2 = FaceEdge.fromWorld(faces.getRight(), face);
	//				final FaceCorner corner = FaceCorner.find(e1, e2);
	//
	//				switch(corner) {
	//				case TOP_LEFT:
	//				default:
	//					return 0;
	//				case TOP_RIGHT:
	//					return divisionLevel == 2 ? 0 : 1;
	//				case BOTTOM_LEFT:
	//					return divisionLevel == 2 ? 1 : 2;
	//				case BOTTOM_RIGHT:
	//					return divisionLevel == 2 ? 1 : 3;
	//				}
	//			}
	//		}
	//
	//		return 0;
	//	}
}
