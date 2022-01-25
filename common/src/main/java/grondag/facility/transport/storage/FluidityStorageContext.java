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

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.Store;

public abstract class FluidityStorageContext implements TransportStorageContext {
	int nextSupplyHandle = 0;

	Article lastSupply = Article.NOTHING;

	protected Store store;

	protected abstract Store store();

	@Override
	public boolean prepareForTick() {
		store = store();
		return store.isValid();
	}

	@Override
	public boolean canAccept(Article targetArticle) {
		return store.getConsumer().canApply(targetArticle);
	}

	@Override
	public Article proposeSupply(ArticleType<?> type) {
		if (store.isEmpty() || !store.allowsType(type).mayBeTrue) {
			return Article.NOTHING;
		}

		if (lastSupply.isNothing() || lastSupply.type() != type || !store.getSupplier().canApply(lastSupply)) {
			// can't reuse last result so iterate content
			final int limit = store.handleCount();

			if (nextSupplyHandle >= limit) {
				nextSupplyHandle = 0;
			}

			final StoredArticleView v = store.view(nextSupplyHandle++);
			lastSupply = v.article().type() == type && !v.isEmpty() ? v.article() : Article.NOTHING;
		}

		return lastSupply;
	}

	@Override
	public Article proposeAccept(ArticleType<?> type) {
		// Fluidity stores will accept anything by default
		return (store.isFull() || !store.allowsType(type).mayBeTrue) ? null : Article.NOTHING;
	}

	@Override
	public void advanceAcceptProposal(ArticleType<?> articleType) {
		// NOOP
	}

	@Override
	public long unitsFor(Article article) {
		return article.type().isBulk() ? 1000 : 1;
	}

	@Override
	public long capacityFor(Article article, long divisor) {
		return store.getConsumer().apply(article, Long.MAX_VALUE, divisor, true);
	}

	@Override
	public long accept(Article article, long numerator, long divisor) {
		return store.getConsumer().apply(article, numerator, divisor, false);
	}

	@Override
	public boolean canSupply(Article article) {
		return store.getSupplier().canApply(article);
	}

	@Override
	public long available(Article article, long divisor) {
		return store.getSupplier().apply(article, Long.MAX_VALUE, divisor, true);
	}

	@Override
	public long supply(Article article, long numerator, long divisor) {
		return store.getSupplier().apply(article, numerator, divisor, false);
	}
}
