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
import grondag.fluidity.api.article.ArticleType;

public abstract class InventoryStorageContext<T extends Container> implements TransportStorageContext {
	protected int supplySlotIndex = 0;
	protected Article lastSupplyArticle = Article.NOTHING;

	protected int acceptSlotIndex = 0;
	protected Article lastAcceptArticle = Article.NOTHING;

	protected int proposalSlotIndex = 0;
	protected Article lastProposalArticle = Article.NOTHING;

	protected T inventory;

	protected int[] slots = new int[0];

	protected abstract T inventory();

	@Override
	public boolean prepareForTick() {
		inventory = inventory();
		setupSlots();
		return inventory != null && slots.length > 0;
	}

	protected void setupSlots() {
		final int limit = inventory == null ? 0 : inventory.getContainerSize();

		if (slots.length != limit) {
			slots = new int[limit];

			for (int i = 0; i < limit; ++i) {
				slots[i] = i;
			}
		}
	}

	@Override
	public boolean canAccept(Article article) {
		return positionToAccept(article);
	}

	protected boolean positionToAccept(Article article) {
		if (article.isNothing()) {
			return false;
		}

		final T inv = inventory;

		if (inv == null) {
			return false;
		}

		final int[] slots = this.slots;
		final int limit = slots.length;

		// restart position search from beginning if different article
		if (acceptSlotIndex < limit && article.equals(lastAcceptArticle) && canPlaceInSlot(article, slots[acceptSlotIndex])) {
			return true;
		}

		for (int i = 0; i < limit; ++i) {
			if (canPlaceInSlot(article, slots[i])) {
				lastAcceptArticle = article;
				acceptSlotIndex = i;
				return true;
			}
		}

		lastAcceptArticle = Article.NOTHING;
		return false;
	}

	protected boolean canPlaceInSlot(Article article, int slot) {
		return InventoryHelper.canPlaceInSlot(article, inventory, slot);
	}

	protected boolean canPlaceInSlot(ItemStack stack, int slot) {
		return InventoryHelper.canPlaceInSlot(stack, inventory, slot);
	}

	@Override
	public long unitsFor(Article article) {
		return 1;
	}

	@Override
	public long capacityFor(Article article, long divisor) {
		if (divisor != 1 || !positionToAccept(article)) {
			return 0;
		}

		final ItemStack stack = inventory.getItem(slots[acceptSlotIndex]);

		assert article.matches(stack) || stack.isEmpty();

		return stack.getMaxStackSize() - stack.getCount();
	}

	@Override
	public Article proposeAccept(ArticleType<?> type) {
		if (type != ArticleType.ITEM) {
			return null;
		}

		final Container inv = inventory;

		if (inv == null) {
			return null;
		}

		final int[] slots = this.slots;
		final int limit = slots.length;

		if (limit == 0) {
			return null;
		}

		// restart position search from beginning on wrap
		if (proposalSlotIndex >= limit) {
			proposalSlotIndex = 0;
		}

		ItemStack stack = inv.getItem(slots[proposalSlotIndex]);

		if (!stack.isEmpty() && stack.getCount() >= stack.getMaxStackSize() && !canPlaceInSlot(stack, proposalSlotIndex)) {
			if (++proposalSlotIndex < limit) {
				stack = inv.getItem(slots[proposalSlotIndex]);
			} else {
				return null;
			}
		}

		if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
			if (!lastProposalArticle.matches(stack)) {
				lastProposalArticle = Article.of(stack);
			}

			return lastProposalArticle;
		} else {
			return null;
		}
	}

	@Override
	public void advanceAcceptProposal(ArticleType<?> articleType) {
		++proposalSlotIndex;
	}

	@Override
	public long accept(Article article, long numerator, long divisor) {
		if (divisor != 1 || !positionToAccept(article)) {
			return 0;
		}

		final Container inv = inventory;
		final ItemStack stack = inv.getItem(slots[acceptSlotIndex]);

		assert article.matches(stack) || stack.isEmpty();

		final long count = Math.min(numerator, stack.getMaxStackSize() - stack.getCount());

		if (count > 0) {
			if (stack.isEmpty()) {
				inv.setItem(slots[acceptSlotIndex], article.toStack(count));
			} else {
				stack.grow((int) count);
			}

			inv.setChanged();
		}

		return count;
	}

	@Override
	public Article proposeSupply(ArticleType<?> type) {
		if (type != ArticleType.ITEM) {
			return Article.NOTHING;
		}

		final Container inv = inventory;

		if (inv == null) {
			return Article.NOTHING;
		}

		final int[] slots = this.slots;
		final int limit = slots.length;

		if (limit == 0) {
			return Article.NOTHING;
		}

		// restart position search from beginning on wrap
		if (supplySlotIndex >= limit) {
			supplySlotIndex = 0;
		}

		ItemStack stack = inv.getItem(slots[supplySlotIndex]);

		if ((stack.isEmpty() || !canExtract(stack, slots[supplySlotIndex]))) {
			if (++supplySlotIndex < limit) {
				stack = inv.getItem(slots[supplySlotIndex]);
			} else {
				return Article.NOTHING;
			}
		}

		if (stack.isEmpty() || !canExtract(stack, slots[supplySlotIndex])) {
			lastSupplyArticle = Article.NOTHING;
		} else if (!lastSupplyArticle.matches(stack)) {
			lastSupplyArticle = Article.of(stack);
		}

		return lastSupplyArticle;
	}

	protected boolean canExtract(ItemStack stack, int slot) {
		return true;
	}

	protected boolean positionToSupply(Article article) {
		final Container inv = inventory;

		if (inv == null) {
			return false;
		}

		final int[] slots = this.slots;
		final int limit = slots.length;

		// restart position search from beginning if different article
		if (supplySlotIndex < limit && article.equals(lastSupplyArticle)) {
			final ItemStack stack = inv.getItem(slots[supplySlotIndex]);

			if (!stack.isEmpty() && article.matches(stack)) {
				return true;
			}
		}

		for (int i = 0; i < limit; ++i) {
			final ItemStack stack = inv.getItem(slots[i]);

			if (!stack.isEmpty() && article.matches(stack)) {
				supplySlotIndex = i;
				lastSupplyArticle = article;
				return true;
			}
		}

		lastSupplyArticle = Article.NOTHING;
		return false;
	}

	@Override
	public boolean canSupply(Article article) {
		return positionToSupply(article);
	}

	@Override
	public long available(Article article, long divisor) {
		if (divisor != 1 || !positionToSupply(article)) {
			return 0;
		}

		final Container inv = inventory;
		final ItemStack stack = inv.getItem(slots[supplySlotIndex]);

		assert !stack.isEmpty() && article.matches(stack);

		return stack.getCount();
	}

	@Override
	public long supply(Article article, long numerator, long divisor) {
		if (divisor != 1 || !positionToSupply(article)) {
			return 0;
		}

		final Container inv = inventory;
		final ItemStack stack = inv.getItem(slots[supplySlotIndex]);

		assert !stack.isEmpty() && article.matches(stack);

		final long count = Math.min(numerator, stack.getCount());

		stack.shrink((int) count);

		if (stack.isEmpty()) {
			inv.setItem(slots[supplySlotIndex], ItemStack.EMPTY);
		}

		inv.setChanged();

		return count;
	}
}
