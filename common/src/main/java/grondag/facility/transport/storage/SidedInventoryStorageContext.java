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

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

import grondag.fluidity.api.article.Article;

public abstract class SidedInventoryStorageContext extends InventoryStorageContext<WorldlyContainer> {
	protected final Direction targetFace;

	public SidedInventoryStorageContext(Direction targetFace) {
		this.targetFace = targetFace;
	}

	@Override
	public boolean prepareForTick() {
		if (super.prepareForTick() && inventory instanceof WorldlyContainer) {
			slots = inventory.getSlotsForFace(targetFace);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean canExtract(ItemStack stack, int slot) {
		return inventory.canTakeItemThroughFace(slot, stack, targetFace);
	}

	@Override
	protected boolean canPlaceInSlot(Article article, int slot) {
		return inventory.canPlaceItemThroughFace(slot, article.toStack(), targetFace);
	}

	@Override
	protected boolean canPlaceInSlot(ItemStack stack, int slot) {
		return inventory.canPlaceItemThroughFace(slot, stack, targetFace);
	}
}
