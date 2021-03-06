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
package grondag.facility.storage.bulk;

import grondag.facility.Facility;
import grondag.facility.storage.FactilityStorageScreenHandler;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.BulkStorageServerDelegate;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TankContainer extends FactilityStorageScreenHandler<BulkStorageServerDelegate> {
	public static Identifier ID = Facility.REG.id("tank");

	public TankContainer(ScreenHandlerType<?> type, PlayerEntity player, int synchId, @Nullable Store storage, String label) {
		super(type, player, synchId, storage, label);
	}

	@Override
	protected BulkStorageServerDelegate createDelegate(ServerPlayerEntity player, Store storage) {
		return new BulkStorageServerDelegate(player, storage);
	}
}
