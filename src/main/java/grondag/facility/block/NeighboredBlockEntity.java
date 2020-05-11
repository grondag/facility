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

import java.util.Arrays;

import javax.annotation.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import grondag.fermion.world.WorldTaskManager;

public abstract class NeighboredBlockEntity<T> extends BlockEntity {
	private final Object[] neighbors = new Object[12];
	private int neighborCount;
	protected boolean isEnqued = false;

	public NeighboredBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@SuppressWarnings("unchecked")
	private void refreshNeighbor(Mutable searchPos, Direction side) {
		final BlockEntity be = world.isChunkLoaded(searchPos) ? world.getBlockEntity(searchPos) : null;
		final int i = side.ordinal();
		final T existing = (T) neighbors[6 + i];

		if(be != null || existing != null) {
			final T result = refreshNeighbor(existing, be, searchPos,  side);
			neighbors[6 + i] = result;

			if (result != null) {
				neighbors[neighborCount++] = result;
			}
		}
	}

	protected boolean hasNeighbors() {
		return neighborCount > 0;
	}

	protected int neighborCount() {
		return neighborCount;
	}

	@SuppressWarnings("unchecked")
	protected T getNeighbor(int i) {
		return (T) neighbors[i];
	}

	protected abstract T refreshNeighbor(@Nullable T existing, @Nullable BlockEntity be, BlockPos neighborPos,  Direction neighborSide);

	protected abstract void onClose(T existing);

	@Override
	public void setLocation(World world, BlockPos blockPos) {
		super.setLocation(world, blockPos);
		enqueUpdate();
	}

	protected void enqueUpdate() {
		if(!isEnqued && !world.isClient) {
			isEnqued = true;
			WorldTaskManager.enqueueImmediate(this::updateNeighbors);
		}
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		enqueUpdate();
	}

	@Override
	public void cancelRemoval() {
		super.cancelRemoval();
		enqueUpdate();
	}

	@Override
	public void markInvalid() {
		super.markInvalid();
		enqueUpdate();
	}

	@SuppressWarnings("unchecked")
	private void closeAll() {
		final int limit = neighborCount;

		if(limit > 0) {
			for(int i = 0; i < limit; i++) {
				onClose((T) neighbors[i]);
			}

			neighborCount = 0;
			Arrays.fill(neighbors, null);
		}
	}

	public void updateNeighbors() {
		if(world == null || world.isClient) {
			return;
		}

		isEnqued = false;

		if(isRemoved()) {
			closeAll();
			return;
		}

		final long myPos = pos.asLong();
		neighborCount = 0;

		final BlockPos.Mutable p = SEARCH_POS.get();
		refreshNeighbor(p.set(myPos).move(Direction.EAST), Direction.WEST);
		refreshNeighbor(p.set(myPos).move(Direction.WEST), Direction.EAST);
		refreshNeighbor(p.set(myPos).move(Direction.NORTH), Direction.SOUTH);
		refreshNeighbor(p.set(myPos).move(Direction.SOUTH), Direction.NORTH);
		refreshNeighbor(p.set(myPos).move(Direction.UP), Direction.DOWN);
		refreshNeighbor(p.set(myPos).move(Direction.DOWN), Direction.UP);

		// Don't hold refs in list-indexed entries (face indexed will already all be set or nulled)
		Arrays.fill(neighbors, neighborCount, 6, null);
	}

	private static final ThreadLocal<BlockPos.Mutable> SEARCH_POS = ThreadLocal.withInitial(BlockPos.Mutable::new);
}
