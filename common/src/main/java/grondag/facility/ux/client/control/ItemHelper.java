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

package grondag.facility.ux.client.control;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemHelper {
	// for unit testing
	public static class TestItemStack {
		public Item item;
		public int size;
		public CompoundTag tag;
		public int meta;

		public TestItemStack(Item item, int size, int meta, CompoundTag tag) {
			this.item = item;
			this.size = size;
			this.meta = meta;
			this.tag = tag;
		}

		public Item getItem() {
			return item;
		}

		public boolean hasTagCompound() {
			return tag != null;
		}

		public CompoundTag getTagCompound() {
			return tag;
		}

		public int getMetadata() {
			return meta;
		}

		public boolean areCapsCompatible(TestItemStack stack1) {
			return true;
		}
	}

	public static CompoundTag getOrCreateStackTag(ItemStack stack) {
		CompoundTag result = stack.getTag();

		if (result == null) {
			result = new CompoundTag();
			stack.setTag(result);
		}

		return result;
	}

	/**
	 * True if item stacks can stack with each other - does not check for stack
	 * limit.
	 */
	public static boolean canStacksCombine(ItemStack stack1, ItemStack stack2) {
		if (stack1.isEmpty()) {
			return false;
		} else if (stack1.getItem() != stack2.getItem()) {
			return false;
		} else if (stack1.hasTag() ^ stack2.hasTag()) {
			return false;
		} else if (stack1.hasTag() && !stack1.getTag().equals(stack2.getTag())) {
			return false;
		} else if (stack1.getCount() != stack2.getCount()) {
			return false;
		}

		return true;
	}

	/**
	 * Returns hash codes that should be equal if
	 * {@link #canStacksCombine(ItemStack, ItemStack)} returns true; Does not
	 * consider capabilities in hash code.
	 */
	//    public static int stackHashCode(ItemStack stack)
	public static int stackHashCode(TestItemStack stack) {
		final Item item = stack.getItem();

		if (item == null) {
			return 0;
		}

		int hash = item.hashCode();

		if (stack.hasTagCompound()) {
			hash = hash * 7919 + stack.getTagCompound().hashCode();
		}

		if (stack.getMetadata() != 0) {
			hash = hash * 7919 + stack.getMetadata();
		}

		return hash;
	}
}
