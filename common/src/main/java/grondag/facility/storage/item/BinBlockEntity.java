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

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.AbstractStore;

public class BinBlockEntity extends CrateBlockEntity {
	protected final int divisionLevel;
	protected final Article[] items;

	public BinBlockEntity(BlockEntityType<BinBlockEntity> type, BlockPos pos, BlockState state, @SuppressWarnings("rawtypes") Supplier<AbstractStore> storageSupplier, String labelRoot, int divisionLevel) {
		super(type, pos, state, storageSupplier, labelRoot);
		this.divisionLevel = divisionLevel;
		items = new Article[divisionLevel];
		Arrays.fill(items, Article.NOTHING);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		if (this.hasLevel() && this.level.isClientSide) {
			boolean hasAny = false;
			final CrateClientState clientState = clientState();
			ItemStack[] renderStacks = clientState.renderStacks;

			for (int i = 0; i < divisionLevel; i++) {
				final Article newItem = Article.fromTag(tag.get("i" + i));

				if (!Objects.equals(newItem, items[i])) {
					items[i] = newItem;
					hasAny = true;

					if (renderStacks == null) {
						renderStacks = new ItemStack[divisionLevel];
					}

					renderStacks[i] = newItem.toStack();
				}
			}

			clientState.renderStacks = hasAny ? renderStacks : null;
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		final CompoundTag result = super.getUpdateTag();

		for (int i = 0; i < divisionLevel; i++) {
			result.put("i" + i, items[i].toTag());
		}

		return result;
	}

	@Override
	public void updateNeighbors() {
		super.updateNeighbors();

		if (!level.isClientSide) {
			refreshClient();
		}
	}

	@Override
	protected void markForSave() {
		super.markForSave();

		if (level != null && worldPosition != null) {
			refreshClient();
		}
	}

	protected void refreshClient() {
		boolean clientRefresh = false;
		final Store storage = getInternalStorage();

		for (int i = 0; i < divisionLevel; i++) {
			final StoredArticleView newView = storage.view(i);
			final Article newItem = newView == null || newView.isEmpty() ? Article.NOTHING : newView.article();

			if (!newItem.equals(items[i])) {
				items[i] = newItem;
				clientRefresh = true;
			}
		}

		if (clientRefresh) {
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
		}
	}
}
