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

package grondag.facility.storage.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import grondag.facility.Facility;
import grondag.facility.storage.FacilityStorageScreenHandler;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;

public class CrateItemScreenHandler extends FacilityStorageScreenHandler<DiscreteStorageServerDelegate> {
	public static ResourceLocation ID = Facility.id("crate_item");
	protected final Slot storeSlot;
	protected final ItemStack storeStack;

	public CrateItemScreenHandler(MenuType<?> type, Player player, int synchId, @Nullable Store storage, String label, ItemStack storeStack) {
		super(type, player, synchId, storage, label);
		Slot slot = null;

		for (final Slot s : slots) {
			if (s.getItem() == storeStack) {
				slot = s;
				break;
			}
		}

		this.storeStack = storeStack;
		storeSlot = slot;
	}

	@Override
	protected DiscreteStorageServerDelegate createDelegate(ServerPlayer player, Store storage) {
		return new DiscreteStorageServerDelegate(player, storage);
	}

	@Override
	public ItemStack quickMoveStack(Player playerEntity, int slotId) {
		// prevent moving stack-based stores
		if (slotId >= 0 && ((storeSlot != null && slotId == storeSlot.index) || (storeStack != null && slots.get(slotId).getItem() == storeStack))) {
			return ItemStack.EMPTY;
		}

		return super.quickMoveStack(playerEntity, slotId);
	}

	@Override
	public void clicked(int slotId, int mouseButton, ClickType slotActionType, Player playerEntity) {
		// prevent moving stack-based stores
		if (slotId >= 0 && ((storeSlot != null && slotId == storeSlot.index) || (storeStack != null && slots.get(slotId).getItem() == storeStack))) {
			return;
		}

		super.clicked(slotId, mouseButton, slotActionType, playerEntity);
	}
}
