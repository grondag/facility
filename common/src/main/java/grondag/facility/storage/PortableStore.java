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

package grondag.facility.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import grondag.fluidity.api.device.ItemComponentContext;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.AbstractPortableStore;

public class PortableStore extends AbstractPortableStore {
	public PortableStore(Store wrapped) {
		super(wrapped);
	}

	public PortableStore(Store wrapped, ItemComponentContext ctx) {
		super(wrapped, ctx);
	}

	public PortableStore(Store wrapped, java.util.function.Supplier<ItemStack> stackGetter, java.util.function.Consumer<ItemStack> stackSetter) {
		super(wrapped, stackGetter, stackSetter);
	}

	@Override
	protected CompoundTag readTagFromStack(ItemStack stack) {
		return stack.getOrCreateTagElement("BlockEntityTag").getCompound(StorageBlockEntity.TAG_STORAGE);
	}

	@Override
	protected void writeTagToStack(ItemStack stack, CompoundTag tag) {
		if (isEmpty()) {
			stack.setTag(null);
		} else {
			stack.getOrCreateTagElement("BlockEntityTag").put(StorageBlockEntity.TAG_STORAGE, tag);
			writeDamage(stack, this);
		}
	}

	public static void writeDamage(ItemStack stack, Store store) {
		final int max = stack.getMaxDamage();
		stack.setDamageValue(store.isEmpty() ? 0 : (max - (int) (store.usage() * (max - 1))));
	}
}
