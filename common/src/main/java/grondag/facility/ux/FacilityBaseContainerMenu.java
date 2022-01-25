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

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.AbstractStorageServerDelegate;
import grondag.fluidity.base.synch.StorageContainer;

public abstract class FacilityBaseContainerMenu<T extends AbstractStorageServerDelegate<?>> extends AbstractContainerMenu implements StorageContainer {
	protected final @Nullable Store storage;
	protected String label;
	protected T delegate;

	public FacilityBaseContainerMenu(MenuType<?> type, Player player, int synchId, @Nullable Store storage, String label) {
		super(type, synchId);
		this.storage = storage;
		this.label = label;
		final Container inv = player.getInventory();

		if (player instanceof ServerPlayer) {
			delegate = createDelegate((ServerPlayer) player, storage);
		}

		for (int p = 0; p < 3; ++p) {
			for (int o = 0; o < 9; ++o) {
				addSlot(new Slot(inv, o + p * 9 + 9, o * 18, p * 18));
			}
		}

		for (int p = 0; p < 9; ++p) {
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

		if (delegate != null) {
			delegate.sendUpdates();
		}
	}

	@Override
	public void removed(Player playerEntity) {
		super.removed(playerEntity);

		if (delegate != null) {
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

			if (playerEntity instanceof ServerPlayer) {
				final int qty = (int) storage.getConsumer().apply(sourceStack, false);

				if (qty < sourceStack.getCount()) {
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
