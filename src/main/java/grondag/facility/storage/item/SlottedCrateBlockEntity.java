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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import grondag.fluidity.base.storage.AbstractStore;
import grondag.fluidity.base.storage.discrete.SlottedInventoryStore;

public class SlottedCrateBlockEntity extends CrateBlockEntity implements Inventory {
	protected final SlottedInventoryStore slottedStore;

	public SlottedCrateBlockEntity(BlockEntityType<? extends CrateBlockEntity> type, BlockPos pos, BlockState state, @SuppressWarnings("rawtypes") Supplier<AbstractStore> storageSupplier, String labelRoot) {
		super(type, pos, state, storageSupplier, labelRoot);
		slottedStore = (SlottedInventoryStore) storage;
	}

	@Override
	public void clear() {
		slottedStore.clear();
	}

	@Override
	public int size() {
		return slottedStore.size();
	}

	@Override
	public boolean isEmpty() {
		return slottedStore.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return slottedStore.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int count) {
		return slottedStore.removeStack(slot, count);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return slottedStore.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack itemStack) {
		slottedStore.setStack(slot, itemStack);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity playerEntity) {
		return slottedStore.canPlayerUse(playerEntity);
	}

	@Override
	public void markDirty() {
		slottedStore.markDirty();
		super.markDirty();
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return !stack.hasTag() || Block.getBlockFromItem(stack.getItem()).getClass() != CrateBlock.class;
	}
}
