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

package grondag.facility.ux.client.color;

import java.util.Locale;

import net.minecraft.client.resources.language.I18n;

/**
 * @deprecated  use ColorUtil
 */
@Deprecated
public enum Luminance {
	BRILLIANT(90),
	EXTRA_BRIGHT(81),
	BRIGHT(72),
	EXTRA_LIGHT(63),
	LIGHT(54),
	MEDIUM_LIGHT(45),
	MEDIUM_DARK(36),
	DARK(27),
	EXTRA_DARK(13);

	public static final Luminance[] VALUES = Luminance.values();
	public static final int COUNT = VALUES.length;

	public final double value;

	Luminance(double luminanceValue) {
		value = luminanceValue;
	}

	public String localizedName() {
		return I18n.get("color.luminance." + name().toLowerCase(Locale.ROOT));
	}
}
