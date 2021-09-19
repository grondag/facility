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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class TankContainer extends FactilityStorageScreenHandler<BulkStorageServerDelegate> {
	public static ResourceLocation ID = Facility.REG.id("tank");

	public TankContainer(MenuType<?> type, Player player, int synchId, @Nullable Store storage, String label) {
		super(type, player, synchId, storage, label);
	}

	@Override
	protected BulkStorageServerDelegate createDelegate(ServerPlayer player, Store storage) {
		return new BulkStorageServerDelegate(player, storage);
	}
}
