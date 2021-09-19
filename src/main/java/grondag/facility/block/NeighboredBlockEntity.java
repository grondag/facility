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
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import grondag.facility.storage.TrackedBlockEntity;
import grondag.fermion.world.WorldTaskManager;

public abstract class NeighboredBlockEntity<T> extends TrackedBlockEntity {
	private final Object[] neighbors = new Object[12];
	private int neighborCount;
	protected boolean isEnqued = false;

	public NeighboredBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
	}

	@SuppressWarnings("unchecked")
	private void refreshNeighbor(MutableBlockPos searchPos, Direction side) {
		final BlockEntity be = level.hasChunkAt(searchPos) ? level.getBlockEntity(searchPos) : null;
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
	public void setLevel(Level world) {
		super.setLevel(world);
		enqueUpdate();
	}

	protected void enqueUpdate() {
		if(!isEnqued && !level.isClientSide) {
			isEnqued = true;
			WorldTaskManager.enqueueImmediate(this::updateNeighbors);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		enqueUpdate();
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved();
		enqueUpdate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
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
		if(level == null || level.isClientSide) {
			return;
		}

		isEnqued = false;

		if(isRemoved()) {
			closeAll();
			return;
		}

		final long myPos = worldPosition.asLong();
		neighborCount = 0;

		final BlockPos.MutableBlockPos p = SEARCH_POS.get();
		refreshNeighbor(p.set(myPos).move(Direction.EAST), Direction.WEST);
		refreshNeighbor(p.set(myPos).move(Direction.WEST), Direction.EAST);
		refreshNeighbor(p.set(myPos).move(Direction.NORTH), Direction.SOUTH);
		refreshNeighbor(p.set(myPos).move(Direction.SOUTH), Direction.NORTH);
		refreshNeighbor(p.set(myPos).move(Direction.UP), Direction.DOWN);
		refreshNeighbor(p.set(myPos).move(Direction.DOWN), Direction.UP);

		// Don't hold refs in list-indexed entries (face indexed will already all be set or nulled)
		Arrays.fill(neighbors, neighborCount, 6, null);
	}

	private static final ThreadLocal<BlockPos.MutableBlockPos> SEARCH_POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
}
