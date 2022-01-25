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

package grondag.facility.transport.storage;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import grondag.fluidity.api.article.Article;

public abstract class InventoryHelper {
	private InventoryHelper() { }

	public static boolean isFull(Container inv) {
		final int limit = inv.getContainerSize();

		for (int i = 0; i < limit; ++i) {
			final ItemStack itemStack = inv.getItem(i);

			if (itemStack.isEmpty() || itemStack.getCount() < itemStack.getMaxStackSize()) {
				return false;
			}
		}

		return true;
	}

	public static boolean canPlaceInSlot(Article article, Container inv, int slot) {
		final ItemStack targetStack = inv.getItem(slot);

		if (targetStack.isEmpty()) {
			return (inv.canPlaceItem(slot, article.toStack()));
		}

		if (article.matches(targetStack) && targetStack.getCount() < targetStack.getMaxStackSize() && inv.canPlaceItem(slot, targetStack)) {
			return true;
		}

		return false;
	}

	public static boolean canPlaceInSlot(ItemStack stack, Container inv, int slot) {
		final ItemStack targetStack = inv.getItem(slot);

		if (targetStack.isEmpty()) {
			return inv.canPlaceItem(slot, stack);
		}

		if (canStacksCombine(targetStack, stack) && inv.canPlaceItem(slot, targetStack)) {
			return true;
		}

		return false;
	}

	public static boolean canStacksCombine(ItemStack targetStack, ItemStack sourceStack) {
		return targetStack.getItem() == sourceStack.getItem()
				&& targetStack.getCount() < targetStack.getMaxStackSize()
				&& ItemStack.tagMatches(targetStack, sourceStack);
	}
}
