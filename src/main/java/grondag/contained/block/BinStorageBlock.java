package grondag.contained.block;

import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.block.BlockAttackInteractionAware;

import grondag.contained.packet.BinActionC2S;
import grondag.fermion.world.WorldHelper;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.orientation.FaceCorner;
import grondag.xm.api.orientation.FaceEdge;

public class BinStorageBlock extends ItemStorageBlock implements BlockAttackInteractionAware {
	protected final int divisionLevel;

	public BinStorageBlock(Block.Settings settings, Supplier<BlockEntity> beFactory, int divisionLevel) {
		super(settings, beFactory);
		this.divisionLevel = divisionLevel;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (state.getBlock() == this && hit.getSide() == state.get(XmProperties.FACE).getOpposite() && hand == Hand.MAIN_HAND) {
			if(world.isClient) {
				BinActionC2S.send(pos, getHitHandle(hit, hit.getSide()), false);
			}

			return ActionResult.SUCCESS;
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}


	protected static class TracerAccess extends Item {
		protected static HitResult trace(World world, PlayerEntity player) {
			return rayTrace(world, player, RayTraceContext.FluidHandling.NONE);
		}

		protected TracerAccess() {
			super(new Settings());
		}
	}

	protected static long lastClickMs = 0;

	@Override
	public boolean onAttackInteraction(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction face) {
		if(world.isClient && state.getBlock() == this) {
			final long t = System.currentTimeMillis();
			final long d = t - lastClickMs;
			lastClickMs = t;

			if(d > 100 && face.getOpposite() == state.get(XmProperties.FACE)) {
				BinActionC2S.send(pos, getHitHandle(world, player, face), true);
			}
		}

		return false;
	}

	protected int getHitHandle(World world, PlayerEntity player, Direction face) {
		return divisionLevel > 1 ? getHitHandle(TracerAccess.trace(world, player), face) : 0;
	}

	protected int getHitHandle(HitResult hit, Direction face) {
		if(divisionLevel > 1) {
			if(hit != null && hit.getType() == HitResult.Type.BLOCK)  {
				final Vec3d vec = hit.getPos();
				final Pair<Direction, Direction> faces = WorldHelper.closestAdjacentFaces(face, vec.x, vec.y, vec.z);
				final FaceEdge e1 = FaceEdge.fromWorld(faces.getLeft(), face);
				final FaceEdge e2 = FaceEdge.fromWorld(faces.getRight(), face);
				final FaceCorner corner = FaceCorner.find(e1, e2);

				switch(corner) {
				case TOP_LEFT:
				default:
					return 0;
				case TOP_RIGHT:
					return divisionLevel == 2 ? 0 : 1;
				case BOTTOM_LEFT:
					return divisionLevel == 2 ? 1 : 2;
				case BOTTOM_RIGHT:
					return divisionLevel == 2 ? 1 : 3;
				}
			}
		}

		return 0;
	}
}
