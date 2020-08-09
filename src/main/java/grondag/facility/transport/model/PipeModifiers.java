package grondag.facility.transport.model;

import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;

import grondag.facility.storage.StorageBlock;
import grondag.facility.transport.PipeBlock;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;

public abstract class PipeModifiers {
	private PipeModifiers() {}

	public static final PrimitiveStateMutator PIPE_CONNECTOR_UPDATE = (modelState, xmBlockState, world, pos, neighbors, refreshFromWorld) -> {
		// join should already be handled, so we just need to check if neighbors are something other than pipes
		if(refreshFromWorld) {
			int bits = 0;
			final SimpleJoinState join = modelState.simpleJoin();

			for(final Direction face : BasePipeModel.FACES) {
				if(join.isJoined(face) && !(neighbors.blockState(face).getBlock() instanceof PipeBlock)) {
					bits |= 1 << face.ordinal();
				}
			}

			modelState.alternateJoinBits(bits);

			final Block block = xmBlockState.getBlock();
			modelState.primitiveBits(block instanceof PipeBlock && ((PipeBlock) block).hasGlow ? BasePipeModel.GLOW_BIT : 0);
		}
	};

	public static final PrimitiveStateMutator STRAIGHT_PIPE_CONNECTOR_UPDATE = (modelState, xmBlockState, world, pos, neighbors, refreshFromWorld) -> {
		// straight pipe always has two joins - connections only apply if some kind of storage
		if(refreshFromWorld) {
			int bits = 0;
			final SimpleJoinState join = modelState.simpleJoin();

			for(final Direction face : BasePipeModel.FACES) {
				if(join.isJoined(face) && neighbors.blockState(face).getBlock() instanceof StorageBlock) {
					bits |= 1 << face.ordinal();
				}
			}

			modelState.alternateJoinBits(bits);

			final Block block = xmBlockState.getBlock();
			modelState.primitiveBits(block instanceof PipeBlock && ((PipeBlock) block).hasGlow ? BasePipeModel.GLOW_BIT : 0);
		}
	};
}
