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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.fluidity.wip.api.transport.CarrierSession;

public abstract class CarrierSessionBlockEntity extends NeighboredBlockEntity<CarrierSession> {
	public CarrierSessionBlockEntity(BlockEntityType<? extends CarrierSessionBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected abstract CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide);

	@Override
	protected CarrierSession refreshNeighbor(CarrierSession existing, BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		if (existing != null) {
			if (be != null && existing.isValid()) {
				return existing;
			} else {
				existing.close();
			}
		}

		if (be != null) {
			final CarrierSession session = getSession(be, neighborPos, neighborSide);

			if (session != null) {
				if (session.isValid()) {
					return session;
				} else {
					session.close();
				}
			}
		}

		return null;
	}

	@Override
	protected void onClose(CarrierSession existing) {
		existing.close();
	}
}
