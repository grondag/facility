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
