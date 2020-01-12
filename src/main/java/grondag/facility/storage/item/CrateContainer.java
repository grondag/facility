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
package grondag.facility.storage.item;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import grondag.facility.Facility;
import grondag.facility.storage.FactilityStorageContainer;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;

public class CrateContainer extends FactilityStorageContainer<DiscreteStorageServerDelegate> {
	public static Identifier ID = Facility.REG.id("item_storage");

	public CrateContainer(PlayerEntity player, int synchId, @Nullable Storage storage, String label) {
		super(player, synchId, storage, label);
	}

	@Override
	protected DiscreteStorageServerDelegate createDelegate(ServerPlayerEntity player, Storage storage) {
		return new DiscreteStorageServerDelegate(player, storage);
	}
}
