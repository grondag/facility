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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import grondag.facility.Facility;
import grondag.facility.storage.FactilityStorageScreenHandler;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;

public class CrateItemScreenHandler extends FactilityStorageScreenHandler<DiscreteStorageServerDelegate> {
	public static Identifier ID = Facility.REG.id("crate_item");
	protected final Slot storeSlot;
	protected final ItemStack storeStack;

	public CrateItemScreenHandler(ScreenHandlerType<?> type, PlayerEntity player, int synchId, @Nullable Store storage, String label, ItemStack storeStack) {
		super(type, player, synchId, storage, label);
		Slot slot = null;

		for(final Slot s : slots) {
			if(s.getStack() == storeStack) {
				slot = s;
				break;
			}
		}

		this.storeStack = storeStack;
		storeSlot = slot;
	}

	@Override
	protected DiscreteStorageServerDelegate createDelegate(ServerPlayerEntity player, Store storage) {
		return new DiscreteStorageServerDelegate(player, storage);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int slotId) {
		// prevent moving stack-based stores
		if(slotId >= 0 && ((storeSlot != null && slotId == storeSlot.id) || (storeStack != null && slots.get(slotId).getStack() == storeStack))) {
			return ItemStack.EMPTY;
		}

		return super.transferSlot(playerEntity, slotId);
	}

	@Override
	public ItemStack onSlotClick(int slotId, int mouseButton, SlotActionType slotActionType, PlayerEntity playerEntity) {
		// prevent moving stack-based stores
		if(slotId >= 0 && ((storeSlot != null && slotId == storeSlot.id) || (storeStack != null && slots.get(slotId).getStack() == storeStack))) {
			return ItemStack.EMPTY;
		}

		return super.onSlotClick(slotId, mouseButton, slotActionType, playerEntity);
	}
}
