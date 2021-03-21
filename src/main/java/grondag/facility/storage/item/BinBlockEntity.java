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
package grondag.facility.storage.item;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

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
	public void fromClientTag(NbtCompound tag) {
		label = tag.getString(TAG_LABEL);

		boolean hasAny = false;
		final CrateClientState clientState = clientState();
		ItemStack[] renderStacks = clientState.renderStacks;

		for(int i = 0; i < divisionLevel; i++) {

			final Article newItem =  Article.fromTag(tag.get("i" + i));

			if(!Objects.equals(newItem, items[i])) {
				items[i] = newItem;
				hasAny = true;

				if(renderStacks == null) {
					renderStacks = new ItemStack[divisionLevel];
				}

				renderStacks[i] = newItem.toStack();
			}
		}

		clientState.renderStacks = hasAny ? renderStacks : null;
	}

	@Override
	public NbtCompound toClientTag(NbtCompound tag) {
		tag.putString(TAG_LABEL, label);

		for(int i = 0; i < divisionLevel; i++) {
			tag.put("i" + i, items[i].toTag());
		}

		return tag;
	}

	@Override
	public void updateNeighbors() {
		super.updateNeighbors();

		if(!world.isClient) {
			refreshClient();
		}
	}

	@Override
	protected void markForSave() {
		super.markForSave();

		if(world != null && pos != null) {
			refreshClient();
		}
	}

	protected void refreshClient() {
		boolean clientRefresh = false;
		final Store storage = getInternalStorage();

		for(int i = 0; i < divisionLevel; i++) {
			final StoredArticleView newView = storage.view(i);
			final Article newItem = newView == null || newView.isEmpty() ? Article.NOTHING : newView.article();

			if (!newItem.equals(items[i])) {
				items[i] = newItem;
				clientRefresh = true;
			}
		}

		if(clientRefresh) {
			sync();
		}
	}
}
