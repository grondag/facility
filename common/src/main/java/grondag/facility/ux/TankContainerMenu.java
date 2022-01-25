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

package grondag.facility.ux;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import grondag.facility.Facility;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.BulkStorageServerDelegate;

public class TankContainerMenu extends FacilityBaseContainerMenu<BulkStorageServerDelegate> {
	public static ResourceLocation ID = Facility.id("tank");

	public TankContainerMenu(MenuType<?> type, Player player, int synchId, @Nullable Store storage, String label) {
		super(type, player, synchId, storage, label);
	}

	@Override
	protected BulkStorageServerDelegate createDelegate(ServerPlayer player, Store storage) {
		return new BulkStorageServerDelegate(player, storage);
	}
}
