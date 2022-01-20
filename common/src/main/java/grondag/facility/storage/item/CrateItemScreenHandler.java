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

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import grondag.facility.Facility;
import grondag.facility.storage.FactilityStorageScreenHandler;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;

public class CrateItemScreenHandler extends FactilityStorageScreenHandler<DiscreteStorageServerDelegate> {
	public static ResourceLocation ID = Facility.id("crate_item");
	protected final Slot storeSlot;
	protected final ItemStack storeStack;

	public CrateItemScreenHandler(MenuType<?> type, Player player, int synchId, @Nullable Store storage, String label, ItemStack storeStack) {
		super(type, player, synchId, storage, label);
		Slot slot = null;

		for(final Slot s : slots) {
			if(s.getItem() == storeStack) {
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
		if(slotId >= 0 && ((storeSlot != null && slotId == storeSlot.index) || (storeStack != null && slots.get(slotId).getItem() == storeStack))) {
			return ItemStack.EMPTY;
		}

		return super.quickMoveStack(playerEntity, slotId);
	}

	@Override
	public void clicked(int slotId, int mouseButton, ClickType slotActionType, Player playerEntity) {
		// prevent moving stack-based stores
		if(slotId >= 0 && ((storeSlot != null && slotId == storeSlot.index) || (storeStack != null && slots.get(slotId).getItem() == storeStack))) {
			return;
		}

		super.clicked(slotId, mouseButton, slotActionType, playerEntity);
	}
}
