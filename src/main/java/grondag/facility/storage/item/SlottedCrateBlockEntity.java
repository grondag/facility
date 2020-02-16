package grondag.facility.storage.item;

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
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.discrete.SlottedInventoryStore;

public class SlottedCrateBlockEntity extends CrateBlockEntity implements Inventory {
	protected final SlottedInventoryStore slottedStore;

	public SlottedCrateBlockEntity(BlockEntityType<? extends CrateBlockEntity> type, Supplier<Store> storageSupplier, String labelRoot) {
		super(type, storageSupplier, labelRoot);
		slottedStore = (SlottedInventoryStore) storage;
	}

	@Override
	public void clear() {
		slottedStore.clear();
	}

	@Override
	public int getInvSize() {
		return slottedStore.getInvSize();
	}

	@Override
	public boolean isInvEmpty() {
		return slottedStore.isInvEmpty();
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slottedStore.getInvStack(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int count) {
		return slottedStore.takeInvStack(slot, count);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return slottedStore.removeInvStack(slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack itemStack) {
		slottedStore.setInvStack(slot, itemStack);
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return slottedStore.canPlayerUseInv(playerEntity);
	}

	@Override
	public void markDirty() {
		slottedStore.markDirty();
		super.markDirty();
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return !stack.hasTag() || Block.getBlockFromItem(stack.getItem()).getClass() != CrateBlock.class;
	}
}
