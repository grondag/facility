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

import grondag.facility.Facility;
import grondag.facility.storage.FactilityStorageScreenHandler;
import grondag.facility.storage.StorageBlockEntity;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CrateScreenHandler extends FactilityStorageScreenHandler<DiscreteStorageServerDelegate> {
	public static Identifier ID = Facility.REG.id("crate");
	protected final  StorageBlockEntity<?, ?> storageBe;

	/** BlockEntity is null on client */
	public CrateScreenHandler(ScreenHandlerType<?> type, PlayerEntity player, int synchId, @Nullable StorageBlockEntity<?, ?> storageBe, String label) {
		super(type, player, synchId, storageBe == null ? null : storageBe.getEffectiveStorage(), label);
		this.storageBe = storageBe;
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return storageBe == null || !storageBe.isRemoved();
	}

	@Override
	protected DiscreteStorageServerDelegate createDelegate(ServerPlayerEntity player, Store storage) {
		return new DiscreteStorageServerDelegate(player, storage);
	}
}
