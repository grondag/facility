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
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.api.transact.Transaction;
import grondag.fluidity.wip.api.transport.CarrierNode;
import grondag.fluidity.wip.base.transport.AssignedNumbersAuthority;

public class ExportBlockEntity extends ItemMoverBlockEntity {
	public ExportBlockEntity(BlockEntityType<ExportBlockEntity> type) {
		super(type);
	}

	protected long sourceAddress = AssignedNumbersAuthority.INVALID_ADDRESS;
	protected Article targetArticle = Article.NOTHING;

	protected CarrierNode lastSource() {
		return sourceAddress == AssignedNumbersAuthority.INVALID_ADDRESS ? CarrierNode.INVALID : internalSession.carrier().nodeByAddress(sourceAddress);
	}

	protected CarrierNode randomSource() {
		final CarrierNode result = internalSession.randomPeer();
		sourceAddress = result.isValid() ? result.nodeAddress() : AssignedNumbersAuthority.INVALID_ADDRESS;
		return result;
	}

	protected CarrierNode sourceFor(Article article) {
		final CarrierNode result = internalSession.supplierOf(article);
		sourceAddress = result.isValid() ? result.nodeAddress() : AssignedNumbersAuthority.INVALID_ADDRESS;
		return result;
	}

	protected Article randomArticle(CarrierNode sourceNode) {
		return sourceNode.isValid() ? sourceNode.getComponent(Store.STORAGE_COMPONENT).get().getAnyArticle().article() : Article.NOTHING;
	}

	@Override
	protected void handleStorage() {
		final Store storage =  Store.STORAGE_COMPONENT.getAccess(world, targetPos).get();

		if(storage == Store.STORAGE_COMPONENT.absent()) {
			resetTickHandler();
			return;
		}

		if(storage.isFull()) {
			return;
		}

		Article targetArticle = this.targetArticle;
		CarrierNode sourceNode = lastSource();

		// try to repeat what we did last time if possible
		if (!targetArticle.isNothing() && sourceNode.isValid()) {
			final long roomFor = storage.getConsumer().apply(targetArticle, Long.MAX_VALUE, true);

			if (roomFor == 0) {
				// need to pick a different article
				targetArticle = Article.NOTHING;
			} else { // roomFor > 0
				final Store sourceStore = sourceNode.getComponent(Store.STORAGE_COMPONENT).get();

				// try existing source first
				final long available = sourceStore.getSupplier().apply(targetArticle, roomFor, true);

				if (available == 0) {
					// existing source is bust, look for a new source
					sourceNode = sourceFor(targetArticle);

					if (!sourceNode.isValid()) {
						// what we want isn't available
						targetArticle = Article.NOTHING;
					}
				}
			}
		}

		// see if we know what we want
		if (targetArticle.isNothing()) {
			if (!storage.isEmpty()) {
				// if we only have a small number of items, favor articles we already have that exist on network
				final int limit = Math.min(16, storage.handleCount());

				for (int i = 0; i < limit; ++i) {
					final Article a = storage.view(i).article();

					if (!a.isNothing()) {
						sourceNode = sourceFor(targetArticle);

						if (sourceNode.isValid()) {
							targetArticle = a;
							break;
						}
					}
				}
			}

			// local storage did not match anything available, so find something random on network
			if (targetArticle.isNothing()) {

				// try existing source first
				targetArticle = randomArticle(sourceNode);

				// if that didn't work, then need a different node
				if (targetArticle.isNothing()) {
					sourceNode = randomSource();
					targetArticle = randomArticle(sourceNode);

					if (targetArticle.isNothing()) {
						// if still nothing, then try again next time
						return;
					}
				}
			}
		}

		this.targetArticle = targetArticle;

		if (!sourceNode.isValid()) {
			sourceNode = sourceFor(targetArticle);
			return;
		}

		final Store sourceStore = sourceNode.getComponent(Store.STORAGE_COMPONENT).get();
		final long roomFor = storage.getConsumer().apply(targetArticle, Long.MAX_VALUE, true);
		final long available = sourceStore.getSupplier().apply(targetArticle, roomFor, true);

		if (available > 0) {
			try (Transaction tx = Transaction.open()) {
				tx.enlist(sourceStore);
				tx.enlist(storage);

				long howMany = carrier.costFunction().apply(internalSession, targetArticle, available, false);
				howMany = sourceStore.getSupplier().apply(targetArticle, howMany, false);

				if (howMany > 0) {
					storage.getConsumer().apply(targetArticle, howMany, false);
					tx.commit();
					cooldownTicks = FacilityConfig.utb1ImporterCooldownTicks;
					return;
				}
			}
		}
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
