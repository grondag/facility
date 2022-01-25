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

import com.google.common.base.Preconditions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.storage.FixedArticleFunction;
import grondag.fluidity.base.article.StoredDiscreteArticle;
import grondag.fluidity.base.storage.discrete.AbstractDiscreteStore;
import grondag.fluidity.base.storage.discrete.DividedDiscreteStore;
import grondag.fluidity.base.storage.discrete.FixedDiscreteStore;
import grondag.fluidity.base.storage.helper.FixedArticleManager;

public class CreativeBinStorage extends AbstractDiscreteStore<CreativeBinStorage> implements FixedDiscreteStore {
	protected final int divisionCount;
	protected final long capacityPerDivision;

	public CreativeBinStorage(int divisionCount, long capacityPerDivision) {
		super(divisionCount, divisionCount * capacityPerDivision, new FixedArticleManager<>(divisionCount, StoredDiscreteArticle::new));
		this.divisionCount = divisionCount;
		this.capacityPerDivision = capacityPerDivision;
	}

	@Override
	public FixedArticleFunction getConsumer() {
		return consumer;
	}

	@Override
	public boolean hasConsumer() {
		return true;
	}

	@Override
	public FixedArticleFunction getSupplier() {
		return supplier;
	}

	@Override
	public boolean hasSupplier() {
		return true;
	}

	@Override
	protected FixedDiscreteArticleFunction createConsumer() {
		return new CreativeBinStorage.Consumer();
	}

	protected class Consumer extends AbstractDiscreteStore<DividedDiscreteStore>.Consumer {
		@Override
		public long apply(Article item, long count, boolean simulate) {
			final StoredDiscreteArticle a = articles.get(item);
			return a == null || a.isEmpty() ? 0 : count;
		}

		@Override
		public long apply(int handle, Article item, long count, boolean simulate) {
			Preconditions.checkArgument(count >= 0, "Request to accept negative items. (%s)", count);
			Preconditions.checkNotNull(item, "Request to accept null item");

			if (item.isNothing() || count == 0 || !filter.test(item)) {
				return 0;
			}

			final StoredDiscreteArticle a = articles.get(handle);

			if (a.isEmpty()) {
				if (!simulate) {
					a.setArticle(item);
					a.setCount(capacityPerDivision);
					notifier.notifyAccept(a, capacityPerDivision);
					dirtyNotifier.run();
				}

				return count;
			} else if (a.article().equals(item)) {
				return count;
			} else {
				return 0;
			}
		}
	}

	@Override
	protected FixedDiscreteArticleFunction createSupplier() {
		return new CreativeBinStorage.Supplier();
	}

	protected class Supplier extends AbstractDiscreteStore<DividedDiscreteStore>.Supplier {
		@Override
		public long apply(Article item, long count, boolean simulate) {
			Preconditions.checkArgument(count >= 0, "Request to supply negative items. (%s)", count);
			Preconditions.checkNotNull(item, "Request to supply null item");

			if (item.isNothing() || isEmpty()) {
				return 0;
			}

			final StoredDiscreteArticle article = articles.get(item);

			return (article == null || article.isEmpty()) ? 0 : count;
		}

		@Override
		public long apply(int handle, Article item, long count, boolean simulate) {
			Preconditions.checkArgument(count >= 0, "Request to supply negative items. (%s)", count);
			Preconditions.checkNotNull(item, "Request to supply null item");

			if (item.isNothing() || isEmpty()) {
				return 0;
			}

			final StoredDiscreteArticle a = articles.get(handle);

			return a == null || a.isEmpty() || !a.article().equals(item) ? 0 : count;
		}
	}

	@Override
	public CompoundTag writeTag() {
		final CompoundTag result = new CompoundTag();

		if (!isEmpty()) {
			final ListTag list = new ListTag();
			final int limit = articles.handleCount();

			for (int i = 0; i < limit; i++) {
				final StoredDiscreteArticle a = articles.get(i);

				if (!a.isEmpty()) {
					final CompoundTag aTag = a.toTag();
					aTag.putInt("handle", i);
					list.add(aTag);
				}
			}

			result.put(AbstractDiscreteStore.TAG_ITEMS, list);
		}

		return result;
	}

	@Override
	public void readTag(CompoundTag tag) {
		clear();

		if (tag.contains(AbstractDiscreteStore.TAG_ITEMS)) {
			final ListTag list = tag.getList(AbstractDiscreteStore.TAG_ITEMS, 10);
			final int limit = list.size();
			final StoredDiscreteArticle lookup = new StoredDiscreteArticle();

			for (int i = 0; i < limit; i++) {
				final CompoundTag aTag = list.getCompound(i);
				lookup.readTag(aTag);

				if (!lookup.isEmpty()) {
					consumer.apply(aTag.getInt("handle"), lookup.article(), lookup.count(), false);
				}
			}
		}
	}
}
