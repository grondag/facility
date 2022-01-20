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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.AbstractStorageServerDelegate;
import grondag.fluidity.base.synch.StorageContainer;

public abstract class FactilityStorageScreenHandler<T extends AbstractStorageServerDelegate<?>> extends AbstractContainerMenu implements StorageContainer {
	protected final @Nullable Store storage;
	protected String label;
	protected T delegate;

	public FactilityStorageScreenHandler(MenuType<?> type, Player player, int synchId, @Nullable Store storage, String label) {
		super(type, synchId);
		this.storage = storage;
		this.label = label;
		final Container inv = player.getInventory();

		if(player instanceof ServerPlayer) {
			delegate = createDelegate((ServerPlayer) player, storage);
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

	protected abstract T createDelegate(ServerPlayer player, Store storage);

	@Override
	public boolean stillValid(Player playerEntity) {
		return storage.isValid();
	}

	@Override
	public Store getStorage() {
		return storage;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		if(delegate != null) {
			delegate.sendUpdates();
		}
	}

	@Override
	public void removed(Player playerEntity) {
		super.removed(playerEntity);

		if(delegate != null) {
			delegate.close(playerEntity);
		}
	}

	@Override
	public boolean clickMenuButton(Player playerEntity, int i) {
		return super.clickMenuButton(playerEntity, i);
	}

	@Override
	public ItemStack quickMoveStack(Player playerEntity, int slotId) {
		final Slot slot = slots.get(slotId);

		if (slot != null && slot.hasItem()) {
			final ItemStack sourceStack = slot.getItem();

			slot.set(ItemStack.EMPTY);
			slot.setChanged();

			if(playerEntity instanceof ServerPlayer) {
				final int qty = (int) storage.getConsumer().apply(sourceStack, false);

				if(qty < sourceStack.getCount()) {
					final ItemStack giveBack = sourceStack.copy();
					giveBack.shrink(qty);
					playerEntity.getInventory().placeItemBackInInventory(giveBack);
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void clicked(int slotId, int mouseButton, ClickType slotActionType, Player playerEntity) {
		super.clicked(slotId, mouseButton, slotActionType, playerEntity);
	}

	@Override
	public void slotsChanged(Container inventory) {
		super.slotsChanged(inventory);
	}

	@Override
	public void setItem(int i, int j, ItemStack itemStack) {
		super.setItem(i, j, itemStack);
	}

	@Override
	public void initializeContents(int i, List<ItemStack> list, ItemStack itemStack) {
		super.initializeContents(i, list, itemStack);
	}

	@Override
	protected boolean moveItemStackTo(ItemStack itemStack, int i, int j, boolean bl) {
		return super.moveItemStackTo(itemStack, i, j, bl);
	}
}
