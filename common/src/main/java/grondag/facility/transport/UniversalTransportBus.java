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

package grondag.facility.transport;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.wip.api.transport.CarrierType;

public class UniversalTransportBus implements CarrierType {
	protected static final Set<ArticleType<?>> articleTypes = ImmutableSet.of(ArticleType.ITEM, ArticleType.FLUID);

	@Override
	public Set<ArticleType<?>> articleTypes() {
		return articleTypes;
	}

	public static UniversalTransportBus BASIC = new UniversalTransportBus();

	public static Set<CarrierType> SET_OF_BASIC = ImmutableSet.of(BASIC);
}
