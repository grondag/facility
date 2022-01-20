package grondag.facility.transport.model;

import grondag.facility.storage.StorageBlock;
import grondag.facility.transport.PipeBlock;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.connect.world.BlockNeighbors;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PipeModifiers {
	private PipeModifiers() {}

	private static int connectorBits(MutablePrimitiveState modelState, BlockNeighbors neighbors) {
		int bits = 0;
		final SimpleJoinState join = modelState.simpleJoin();

		// join should already be handled, so we just need to check if neighbors are storage blocks
		for(final Direction face : BasePipeModel.FACES) {
			if(join.isJoined(face) && neighbors.blockState(face).getBlock() instanceof StorageBlock) {
				bits |= 1 << face.ordinal();
			}
		}

		return bits;
	}

	private static int glowBits(BlockState blockState) {
		final Block block = blockState.getBlock();
		return block instanceof PipeBlock && ((PipeBlock) block).hasGlow ? BasePipeModel.GLOW_BIT : 0;
	}

	public static final PrimitiveStateMutator PIPE_CONNECTOR_UPDATE = (modelState, blockState, world, pos, neighbors, refreshFromWorld) -> {
		if(refreshFromWorld) {
			modelState.alternateJoinBits(connectorBits(modelState, neighbors));
			modelState.primitiveBits(glowBits(blockState));
		}
	};

	public static final PrimitiveStateMutator MOVER_CONNECTOR_UPDATE = (modelState, blockState, world, pos, neighbors, refreshFromWorld) -> {
		if(refreshFromWorld) {
			int bits = connectorBits(modelState, neighbors);
			// movers always connect on target face
			bits |= 1 << blockState.getValue(XmProperties.FACE).ordinal();
			modelState.alternateJoinBits(bits);
			modelState.primitiveBits(glowBits(blockState));
		}
	};
}
