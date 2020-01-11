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
package grondag.facility.storage;

import grondag.facility.FacilityConfig;

@SuppressWarnings("rawtypes")
public class StorageClientState<T extends StorageBlockEntity> {
	public final T owner;

	/**
	 * on client, caches last result from
	 */
	private long lastDistanceSquared;

	private float displayAlpha = 1f;

	public StorageClientState(T owner) {
		this.owner = owner;
	}

	public void updateLastDistanceSquared(double dSq) {
		final long d = (long) dSq;
		final long lastD = lastDistanceSquared;

		if(d != lastD) {
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
