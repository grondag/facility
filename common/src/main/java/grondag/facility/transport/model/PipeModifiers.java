/*
 * This file is part of Facility and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.facility.transport.model;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import grondag.facility.storage.StorageBlock;
import grondag.facility.transport.PipeBlock;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.connect.world.BlockNeighbors;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;

public abstract class PipeModifiers {
	private PipeModifiers() { }

	private static int connectorBits(MutablePrimitiveState modelState, BlockNeighbors neighbors) {
		int bits = 0;
		final SimpleJoinState join = modelState.simpleJoin();

		// join should already be handled, so we just need to check if neighbors are storage blocks
		for (final Direction face : BasePipeModel.FACES) {
			if (join.isJoined(face) && neighbors.blockState(face).getBlock() instanceof StorageBlock) {
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
		if (refreshFromWorld) {
			modelState.alternateJoinBits(connectorBits(modelState, neighbors));
			modelState.primitiveBits(glowBits(blockState));
		}
	};

	public static final PrimitiveStateMutator MOVER_CONNECTOR_UPDATE = (modelState, blockState, world, pos, neighbors, refreshFromWorld) -> {
		if (refreshFromWorld) {
			int bits = connectorBits(modelState, neighbors);
			// movers always connect on target face
			bits |= 1 << blockState.getValue(XmProperties.FACE).ordinal();
			modelState.alternateJoinBits(bits);
			modelState.primitiveBits(glowBits(blockState));
		}
	};
}
