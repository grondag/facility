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
package grondag.facility.storage;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import grondag.facility.Facility;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.synch.ItemStorageServerDelegate;
import grondag.fluidity.base.synch.StorageContainer;

public class CrateContainer extends Container implements StorageContainer {
	public static Identifier ID = Facility.REG.id("item_storage");

	protected final @Nullable Storage storage;
	protected String label;
	protected ItemStorageServerDelegate delegate;

	public CrateContainer(PlayerEntity player, int synchId, @Nullable Storage storage, String label) {
		super(null, synchId);
		this.storage = storage;
		this.label = label;
		final Inventory inv = player.inventory;

		if(player instanceof ServerPlayerEntity) {
			delegate = new ItemStorageServerDelegate((ServerPlayerEntity) player, storage);
		}

		for(int p = 0; p < 3; ++p) {
			for(int o = 0; o < 9; ++o) {
				addSlot(new Slot(inv, o + p * 9 + 9, o * 18, p * 18));
			}
		}

		for(int p = 0; p < 9; ++p) {
			addSlot(new Slot(inv, p, p * 18, 58));
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public Storage getStorage() {
		return storage;
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		if(delegate != null) {
			delegate.sendUpdates();
		}
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);

		if(delegate != null) {
			delegate.close(playerEntity);
		}
	}

	@Override
	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		return super.onButtonClick(playerEntity, i);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int slotId) {
		final Slot slot = slotList.get(slotId);

		if (slot != null && slot.hasStack()) {
			final ItemStack sourceStack = slot.getStack();

			slot.setStack(ItemStack.EMPTY);
			slot.markDirty();

			if(playerEntity instanceof ServerPlayerEntity) {
				final int qty = (int) storage.getConsumer().accept(sourceStack, false);

				if(qty < sourceStack.getCount()) {
					final ItemStack giveBack = sourceStack.copy();
					giveBack.decrement(qty);
					playerEntity.inventory.offerOrDrop(playerEntity.world, giveBack);
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack onSlotClick(int slotId, int mouseButton, SlotActionType slotActionType, PlayerEntity playerEntity) {
		return super.onSlotClick(slotId, mouseButton, slotActionType, playerEntity);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);
	}

	@Override
	public void setStackInSlot(int i, ItemStack itemStack) {
		super.setStackInSlot(i, itemStack);
	}

	@Override
	public void updateSlotStacks(List<ItemStack> list) {
		super.updateSlotStacks(list);
	}

	@Override
	protected boolean insertItem(ItemStack itemStack, int i, int j, boolean bl) {
		return super.insertItem(itemStack, i, j, bl);
	}
}
