package grondag.facility.transport.model;

import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;

import grondag.facility.transport.PipeBlock;
import grondag.facility.transport.PipeBlockEntity;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;

public abstract class PipeModifiers {
	private PipeModifiers() {}

	public static final PrimitiveStateMutator OMNI_PIPE_UPDATE = (modelState, xmBlockState, world, pos, neighbors, refreshFromWorld) -> {
		// join should already be handled, so we just need to check if neighbors are inventory
		if(refreshFromWorld) {
			int bits = 0;
			final SimpleJoinState join = modelState.simpleJoin();

			for(final Direction face : BasePipeModel.FACES) {
				if(join.isJoined(face)) {
					if(!(neighbors.blockEntity(face) instanceof PipeBlockEntity)) {
						bits |= 1 << face.ordinal();
					};
				}
			}

			final Block block = xmBlockState.getBlock();

			if (block instanceof PipeBlock && ((PipeBlock) block).hasGlow) {
				bits |= BasePipeModel.GLOW_BIT;
			}

			modelState.primitiveBits(bits);
		}
	};

	public static final PrimitiveStateMutator AXIS_PIPE_UPDATE = (modelState, xmBlockState, world, pos, neighbors, refreshFromWorld) -> {
		// join should already be handled, so we just need to check if neighbors are inventory
		if(refreshFromWorld) {
			int bits = 0;
			final SimpleJoinState join = modelState.simpleJoin();

			for(final Direction face : BasePipeModel.FACES) {
				if(join.isJoined(face)) {
					if(!(neighbors.blockEntity(face) instanceof PipeBlockEntity)) {
						bits |= 1 << face.ordinal();
					};
				}
			}

			final Block block = xmBlockState.getBlock();

			if (block instanceof PipeBlock && ((PipeBlock) block).hasGlow) {
				bits |= BasePipeModel.GLOW_BIT;
			}

			modelState.primitiveBits(bits);
		}
	};

	public static final PrimitiveStateMutator MOVER_PIPE_UPDATE = (modelState, xmBlockState, world, pos, neighbors, refreshFromWorld) -> {
		// join should already be handled, so we just need to check if neighbors are inventory
		if(refreshFromWorld) {
			int bits = 0;
			final SimpleJoinState join = modelState.simpleJoin();

			for(final Direction face : BasePipeModel.FACES) {
				if(join.isJoined(face)) {
					if(!(neighbors.blockEntity(face) instanceof PipeBlockEntity)) {
						bits |= 1 << face.ordinal();
					};
				}
			}

			final Block block = xmBlockState.getBlock();

			if (block instanceof PipeBlock && ((PipeBlock) block).hasGlow) {
				bits |= BasePipeModel.GLOW_BIT;
			}

			modelState.primitiveBits(bits);
		}
	};

}
