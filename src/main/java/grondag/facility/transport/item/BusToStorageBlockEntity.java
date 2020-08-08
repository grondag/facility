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

import net.minecraft.block.entity.BlockEntityType;

import grondag.facility.transport.handler.Bus2StorageTickHandler;
import grondag.facility.transport.handler.TransportContext;

public class BusToStorageBlockEntity extends ItemMoverBlockEntity {
	public BusToStorageBlockEntity(BlockEntityType<BusToStorageBlockEntity> type) {
		super(type);
	}

	@Override
	protected boolean handleStorage(TransportContext context) {
		return Bus2StorageTickHandler.INSTANCE.tick(context);
	}

	@Override
	protected boolean handleVanillaInv(TransportContext context) {
		return true;
	}

	@Override
	protected boolean handleVanillaSidedInv(TransportContext context) {
		return true;
	}
}
