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

package grondag.facility.storage.item;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.fluidity.base.storage.AbstractStore;
import grondag.fluidity.base.storage.discrete.SlottedInventoryStore;

public class SlottedCrateBlockEntity extends CrateBlockEntity implements Container {
	protected final SlottedInventoryStore slottedStore;

	public SlottedCrateBlockEntity(BlockEntityType<? extends CrateBlockEntity> type, BlockPos pos, BlockState state, @SuppressWarnings("rawtypes") Supplier<AbstractStore> storageSupplier, String labelRoot) {
		super(type, pos, state, storageSupplier, labelRoot);
		slottedStore = (SlottedInventoryStore) storage;
	}

	@Override
	public void clearContent() {
		slottedStore.clearContent();
	}

	@Override
	public int getContainerSize() {
		return slottedStore.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return slottedStore.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return slottedStore.getItem(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		return slottedStore.removeItem(slot, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return slottedStore.removeItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack itemStack) {
		slottedStore.setItem(slot, itemStack);
	}

	@Override
	public boolean stillValid(Player playerEntity) {
		return slottedStore.stillValid(playerEntity);
	}

	@Override
	public void setChanged() {
		slottedStore.setChanged();
		super.setChanged();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return !stack.hasTag() || Block.byItem(stack.getItem()).getClass() != CrateBlock.class;
	}
}
