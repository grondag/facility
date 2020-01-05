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
package grondag.facility.transport;

import java.util.Set;

import com.google.common.util.concurrent.Runnables;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.BlockComponentContext;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.SingleCarrierProvider;
import grondag.xm.api.block.XmProperties;

public class IntakeBlockEntity extends PipeBlockEntity implements Tickable, CarrierConnector {
	protected Runnable tickHandler = this::selectRunnable;
	protected BlockPos targetPos = null;
	Direction targetFace = null;
	CarrierSession internalSession;

	public IntakeBlockEntity(BlockEntityType<IntakeBlockEntity> type) {
		super(type);
		internalSession = carrier.attach(this, ct -> ct.get(this));
	}

	@Override
	protected CarrierProvider createCarrierProvider() {
		return SingleCarrierProvider.of(carrier);
	}

	// does not provide carrier to the export side
	@Override
	public CarrierProvider getCarrierProvider(BlockComponentContext ctx) {
		return world == null || pos == null || ctx.side() == getCachedState().get(XmProperties.FACE) ? CarrierProvider.CARRIER_PROVIDER_COMPONENT.absent() : carrierProvider;
	}

	@Override
	public Set<ArticleType<?>> articleTypes() {
		return ArticleType.SET_OF_ITEMS;
	}

	protected void selectRunnable() {
		if(getWorld() == null || getPos() == null) {
			return;
		}

		final Direction face = getCachedState().get(XmProperties.FACE);

		targetPos = getPos().offset(face);
		targetFace = face.getOpposite();

		final Storage storage =  Storage.STORAGE_COMPONENT.get(world, targetPos).get();

		if(storage != Storage.STORAGE_COMPONENT.absent()) {
			tickHandler = this::handleStorage;
			return;
		}

		final Inventory inv = HopperBlockEntity.getInventoryAt(world, targetPos);

		if(inv != null) {
			tickHandler = inv instanceof SidedInventory ? this::handleVanillaSidedInv : this::handleVanillaInv;
			return;
		}

		tickHandler = Runnables.doNothing();
	}

	@Override
	protected void onEnquedUpdate() {
		resetTickHandler();
	}

	public void resetTickHandler() {
		tickHandler = this::selectRunnable;
	}

	protected void handleStorage() {
		final Storage storage =  Storage.STORAGE_COMPONENT.get(world, targetPos).get();

		if(storage == Storage.STORAGE_COMPONENT.absent()) {
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
				if(internalSession.broadcastConsumer().apply(a.article(), howMany, false) != howMany ||
						storage.getSupplier().apply(a.article(), howMany, false) != howMany) {
					// TODO: roll back
				}
			}

			return false;
		});
	}

	protected void handleVanillaInv() {
		final Inventory inv = HopperBlockEntity.getInventoryAt(world, targetPos);

		if(inv == null) {
			resetTickHandler();
			return;
		}

		if(inv.isInvEmpty()) {
			return;
		}

		final int limit = inv.getInvSize();

		if(limit == 0) {
			return;
		}

		for(int slot = 0; slot < limit; slot++) {

			final ItemStack targetStack = inv.getInvStack(slot);

			if (!targetStack.isEmpty()) {
				final int howMany = (int) internalSession.broadcastConsumer().apply(targetStack, false);

				if(howMany > 0) {
					targetStack.decrement(howMany);

					if(targetStack.isEmpty()) {
						inv.setInvStack(slot, ItemStack.EMPTY);
					}

					inv.markDirty();
				}
			}
		}
	}

	protected void handleVanillaSidedInv() {
		final Inventory inv = HopperBlockEntity.getInventoryAt(world, targetPos);

		if(inv == null || !(inv instanceof SidedInventory)) {
			resetTickHandler();
			return;
		}

		if(inv.isInvEmpty()) {
			return;
		}

		final SidedInventory sidedInv = (SidedInventory) inv;

		final int[] slots = sidedInv.getInvAvailableSlots(targetFace);
		final int limit = slots.length;

		if(limit == 0) {
			return;
		}

		for(int i = 0; i < limit; i++) {
			final int slot = slots[i];
			final ItemStack targetStack = sidedInv.getInvStack(slot);

			if (!targetStack.isEmpty() && sidedInv.canExtractInvStack(slot, targetStack, targetFace)) {
				final int howMany = (int) internalSession.broadcastConsumer().apply(targetStack, false);

				if(howMany > 0) {
					targetStack.decrement(howMany);

					if(targetStack.isEmpty()) {
						sidedInv.setInvStack(slot, ItemStack.EMPTY);
					}

					sidedInv.markDirty();
				}
			}
		}
	}

	@Override
	public void tick() {
		if(world.isClient) {
			return;
		}

		// TODO: allow inversion or disable of redstone control
		if(getCachedState().get(Properties.POWERED)) {
			return;
		}

		tickHandler.run();
	}
}
