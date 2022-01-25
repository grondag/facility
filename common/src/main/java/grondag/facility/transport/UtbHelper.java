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

import grondag.facility.FacilityConfig;
import grondag.fluidity.api.article.Article;

public class UtbHelper {
	/**
	 * Limits local transfer operations (buffer to/from storage).
	 */
	public static long throttleUtb1LocalItem(long qty) {
		return Math.min(qty, FacilityConfig.utb1ItemsPerTick);
	}

	/**
	 * Limits local transfer operations (buffer to/from storage).
	 */
	public static long throttleUtb1LocalFluid(long numerator, long divisor) {
		return Math.min(numerator, divisor);
	}

	public static long throttleUtb1Local(Article article, long numerator, long divisor) {
		if (article.isItem() && divisor == 1) {
			return throttleUtb1LocalItem(numerator);
		} else if (article.isFluid()) {
			return throttleUtb1LocalFluid(numerator, divisor);
		} else {
			return 0;
		}
	}
}
