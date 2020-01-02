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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.fluidity.wip.api.transport.CarrierSession;

public abstract class CarrierSessionBlockEntity extends NeighboredBlockEntity<CarrierSession> implements CarrierConnector {
	public CarrierSessionBlockEntity(BlockEntityType<? extends CarrierSessionBlockEntity> type) {
		super(type);
	}

	protected abstract CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide);

	@Override
	protected CarrierSession refreshNeighbor(CarrierSession existing, BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		if(existing != null) {
			if(be != null && existing.isValid()) {
				return existing;
			} else {
				existing.close();
			}
		}

		if(be != null) {
			final CarrierSession session = getSession(be, neighborPos, neighborSide);
			// TODO: remove or make this proper tracing
			//		System.out.println(this.getClass().getCanonicalName() + " @" + pos.toString() + " assigned address " + session.address()
			//		+ (session.isValid() ? " (valid)" : " (INVALID)" + " with carrier " + be.getClass().getCanonicalName()));

			if(session != null) {
				if(session.isValid()) {
					return session;
				} else {
					session.close();
				}
			}
		}

		return  null;
	}

	@Override
	protected void onClose(CarrierSession existing) {
		existing.close();
	}
}
