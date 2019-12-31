/*******************************************************************************
 * Copyright 2019, 2020 grondag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.facility.block;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;

public abstract class NeighboredBlockEntity<T> extends BlockEntity {

	protected final ObjectArrayList<T> neighbors = new ObjectArrayList<>();

	public NeighboredBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	protected void addNeighbor(Mutable searchPos, Direction side) {
		if(world.isChunkLoaded(searchPos)) {
			final BlockEntity be = world.getBlockEntity(searchPos);

			if(be != null) {
				addNeighbor(be, searchPos,  side);
			}
		}
	}

	protected abstract void addNeighbor(BlockEntity be, BlockPos neighborPos,  Direction neighborSide);

	public void updateNeighbors() {
		if(world == null || world.isClient) {
			return;
		}

		neighbors.clear();
		final long myPos = pos.asLong();

		try(BlockPos.PooledMutable p = BlockPos.PooledMutable.get()) {
			addNeighbor(p.set(myPos).setOffset(Direction.EAST), Direction.WEST);
			addNeighbor(p.set(myPos).setOffset(Direction.WEST), Direction.EAST);
			addNeighbor(p.set(myPos).setOffset(Direction.NORTH), Direction.SOUTH);
			addNeighbor(p.set(myPos).setOffset(Direction.SOUTH), Direction.NORTH);
			addNeighbor(p.set(myPos).setOffset(Direction.UP), Direction.DOWN);
			addNeighbor(p.set(myPos).setOffset(Direction.DOWN), Direction.UP);
		}
	}

}
