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

import org.jetbrains.annotations.Nullable;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;

public interface TransportStorageContext {
	boolean prepareForTick();

	boolean canAccept(Article targetArticle);

	/**
	 * Find an item the stora can supply.  Subsequent calls iterate choices. Eventually wraps.
	 * @param type Fluid or Item (or others in future)
	 * @return Article.NOTHING if not supplying anything at this time.
	 */
	Article proposeSupply(ArticleType<?> type);

	/**
	 * Find an item the stora can accept. Subsequent calls iterate choices. Eventually wraps.
	 * @param type Fluid or Item (or others in future)
	 * @return Article.NOTHING if no preference, null if not accepting anything at this time.
	 */
	@Nullable Article proposeAccept(ArticleType<?> type);

	/**
	 * Called by handler when {@link #proposeAccept(ArticleType)} return a non-null, non-nothing
	 * article and that article is not available. Signals storage context to propose something
	 * different if possible.
	 * @param articleType
	 */
	void advanceAcceptProposal(ArticleType<?> articleType);

	long unitsFor(Article targetArticle);

	long capacityFor(Article targetArticle, long divisor);

	long accept(Article storedArticle, long numerator, long divisor);

	boolean canSupply(Article targetArticle);

	long available(Article targetArticle, long units);

	long supply(Article targetArticle, long howMany, long units);
}
