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
package grondag.facility.transport.item;

import grondag.facility.transport.handler.Bus2StorageTickHandler;
import grondag.facility.transport.handler.TransportTickHandler;
import grondag.fluidity.api.storage.ArticleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BusToStorageBlockEntity extends ItemMoverBlockEntity {
	public BusToStorageBlockEntity(BlockEntityType<BusToStorageBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected TransportTickHandler itemTickHandler() {
		return Bus2StorageTickHandler.INSTANCE;
	}

	@Override
	protected TransportTickHandler fluidTickHandler() {
		return Bus2StorageTickHandler.INSTANCE;
	}

	@Override
	public ArticleFunction getConsumer() {
		return transportBuffer.consumer();
	}

	@Override
	protected void tickBuffer() {
		transportBuffer.flushItemToStorage(itemStorage);
		transportBuffer.flushFluidToStorage(fluidStorage);
	}

}
