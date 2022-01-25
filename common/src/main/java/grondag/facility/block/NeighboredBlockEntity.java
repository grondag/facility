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

package grondag.facility.block;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.facility.varia.WorldTaskManager;

public abstract class NeighboredBlockEntity<T> extends BlockEntity {
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

		if (be != null || existing != null) {
			final T result = refreshNeighbor(existing, be, searchPos, side);
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

	protected abstract T refreshNeighbor(@Nullable T existing, @Nullable BlockEntity be, BlockPos neighborPos, Direction neighborSide);

	protected abstract void onClose(T existing);

	private boolean isLoaded = false;

	@Override
	public void setLevel(Level world) {
		super.setLevel(world);

		if (!isLoaded) {
			onLoaded();
			isLoaded = true;
		}

		enqueUpdate();
	}

	protected void enqueUpdate() {
		if (!isEnqued && !level.isClientSide) {
			isEnqued = true;
			WorldTaskManager.enqueueImmediate(this::updateNeighbors);
		}
	}

	protected abstract void onLoaded();

	protected abstract void onUnloaded();

	@Override
	public void setRemoved() {
		if (isLoaded) {
			onUnloaded();
			isLoaded = false;
		}

		super.setRemoved();
		enqueUpdate();
	}

	@Override
	public void clearRemoved() {
		if (!isLoaded) {
			onLoaded();
			isLoaded = true;
		}

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

		if (limit > 0) {
			for (int i = 0; i < limit; i++) {
				onClose((T) neighbors[i]);
			}

			neighborCount = 0;
			Arrays.fill(neighbors, null);
		}
	}

	public void updateNeighbors() {
		if (level == null || level.isClientSide) {
			return;
		}

		isEnqued = false;

		if (isRemoved()) {
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
