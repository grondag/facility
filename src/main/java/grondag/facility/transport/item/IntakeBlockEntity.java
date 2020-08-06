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
package grondag.facility.transport.item;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;

import grondag.facility.FacilityConfig;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.api.transact.Transaction;

public class IntakeBlockEntity extends ItemMoverBlockEntity {
	public IntakeBlockEntity(BlockEntityType<IntakeBlockEntity> type) {
		super(type);
	}

	@Override
	protected void handleStorage() {
		final Store storage =  Store.STORAGE_COMPONENT.getAccess(world, targetPos).get();

		if(storage == Store.STORAGE_COMPONENT.absent()) {
			resetTickHandler();
			return;
		}

		if(storage.isEmpty()) {
			return;
		}

		storage.forEach(a -> !a.isEmpty(), a -> {
			long howMany = internalSession.broadcastConsumer().apply(a.article(), a.count(), true);
			howMany = storage.getSupplier().apply(a.article(), howMany, true);

			if(howMany > 0) {
				cooldownTicks = FacilityConfig.utb1ImporterCooldownTicks;

				try (Transaction tx = Transaction.open()) {
					tx.enlist(internalSession.broadcastConsumer());
					tx.enlist(storage);

					if(internalSession.broadcastConsumer().apply(a.article(), howMany, false) == howMany &&
							storage.getSupplier().apply(a.article(), howMany, false) == howMany) {
						tx.commit();
					}
				}
			}

			return false;
		});
	}

	@Override
	protected void handleVanillaInv() {
		final Inventory inv = HopperBlockEntity.getInventoryAt(world, targetPos);

		if(inv == null) {
			resetTickHandler();
			return;
		}

		if(inv.isEmpty()) {
			return;
		}

		final int limit = inv.size();

		if(limit == 0) {
			return;
		}

		for(int slot = 0; slot < limit; slot++) {

			final ItemStack targetStack = inv.getStack(slot);

			if (!targetStack.isEmpty()) {
				// NB: broadcast consumer ensures atomicity
				final int howMany = (int) internalSession.broadcastConsumer().apply(targetStack, false);

				if(howMany > 0) {
					cooldownTicks = FacilityConfig.utb1ImporterCooldownTicks;
					targetStack.decrement(howMany);

					if(targetStack.isEmpty()) {
						inv.setStack(slot, ItemStack.EMPTY);
					}

					inv.markDirty();
				}
			}
		}
	}

	@Override
	protected void handleVanillaSidedInv() {
		final Inventory inv = HopperBlockEntity.getInventoryAt(world, targetPos);

		if(inv == null || !(inv instanceof SidedInventory)) {
			resetTickHandler();
			return;
		}

		if(inv.isEmpty()) {
			return;
		}

		final SidedInventory sidedInv = (SidedInventory) inv;

		final int[] slots = sidedInv.getAvailableSlots(targetFace);
		final int limit = slots.length;

		if(limit == 0) {
			return;
		}

		for(int i = 0; i < limit; i++) {
			final int slot = slots[i];
			final ItemStack targetStack = sidedInv.getStack(slot);

			if (!targetStack.isEmpty() && sidedInv.canExtract(slot, targetStack, targetFace)) {
				final int howMany = (int) internalSession.broadcastConsumer().apply(targetStack, false);

				if(howMany > 0) {
					cooldownTicks = FacilityConfig.utb1ImporterCooldownTicks;
					targetStack.decrement(howMany);

					if(targetStack.isEmpty()) {
						sidedInv.setStack(slot, ItemStack.EMPTY);
					}

					sidedInv.markDirty();
				}
			}
		}
	}
}
