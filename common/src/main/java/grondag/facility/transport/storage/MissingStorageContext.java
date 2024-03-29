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

import grondag.fluidity.api.storage.Store;

public class MissingStorageContext extends FluidityStorageContext {
	private MissingStorageContext() {
		store = Store.EMPTY;
	}

	public static final FluidityStorageContext INSTANCE = new MissingStorageContext();

	@Override
	protected Store store() {
		return Store.EMPTY;
	}

	@Override
	public boolean prepareForTick() {
		return false;
	}
}
