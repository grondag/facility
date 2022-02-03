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
public enum Chroma {
	PURE_NETURAL(0),
	WHITE(2.5),
	GREY(5),
	NEUTRAL(10),
	RICH(20),
	DEEP(30),
	EXTRA_DEEP(40),
	BOLD(50),
	EXTRA_BOLD(60),
	ACCENT(70),
	INTENSE_ACCENT(80),
	ULTRA_ACCENT(90);

	public static final Chroma[] VALUES = Chroma.values();
	public static final int COUNT = VALUES.length;

	public final double value;

	Chroma(double chromaValue) {
		value = chromaValue;
	}

	public String localizedName() {
		return I18n.get("color.chroma." + name().toLowerCase(Locale.ROOT));
	}
}
