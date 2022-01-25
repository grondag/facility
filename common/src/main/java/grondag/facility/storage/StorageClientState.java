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

package grondag.facility.storage;

import grondag.facility.FacilityConfig;

@SuppressWarnings("rawtypes")
public class StorageClientState<T extends StorageBlockEntity> {
	public final T owner;

	/**
	 * On client, caches last result from distance calc.
	 */
	private long lastDistanceSquared;

	private float displayAlpha = 1f;

	public StorageClientState(T owner) {
		this.owner = owner;
	}

	// FIX: call this or remove it - no longer called as part of render loop
	public void updateLastDistanceSquared(double dSq) {
		final long d = (long) dSq;
		final long lastD = lastDistanceSquared;

		if (d != lastD) {
			// fade effect isn't great, so disable for now
			//			// fade in controls as player approaches - over a 4-block distance
			//			float a  =  1f - ((float) Math.sqrt(d) - 8) * 0.25f; //ContainedConfig.maxRenderDistance) * 0.25f;
			//
			//			if (a < 0) {
			//				a = 0;
			//			} else if (a > 1) {
			//				a = 1;
			//			}

			displayAlpha = d > FacilityConfig.maxRenderDistanceSq ? 0 : 1;
			lastDistanceSquared = d;
		}
	}

	public float displayAlpha() {
		return displayAlpha;
	}

	public String label() {
		return owner.getLabel();
	}
}
