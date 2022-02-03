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
public enum Hue {
	INFRARED,
	CHERRY,
	ROSE,
	POMEGRANATE,
	CRIMSON,
	SCARLET,
	RED,
	VERMILLION,
	TANGERINE,
	ORANGE,
	EMBER,
	SUNSET,
	PUMPKIN,
	CHEDDAR,
	MANGO,
	SUNFLOWER,
	GOLD,
	TORCH,
	YELLOW,
	LEMON,
	LIME,
	PERIDOT,
	CHARTREUSE,
	CACTUS,
	GREEN,
	FOLIAGE,
	MINT,
	SAGE,
	JUNIPER,
	CELADON,
	EMERALD,
	VERDIGRIS,
	TURQUOISE,
	SEA_FOAM,
	CYAN,
	ICE,
	BERYL,
	APATITE,
	MARINE,
	AQUA,
	ROBIN_EGG,
	MORNING,
	CERULEAN,
	TOPAZ,
	SKY,
	SAPPHIRE,
	PERIWINKLE,
	TWILIGHT,
	AZURE,
	OCEAN,
	COBALT,
	BLUE,
	LAPIS,
	INDIGO,
	VIOLET,
	PURPLE,
	AMETHYST,
	LILAC,
	MAGENTA,
	FUSCHIA,
	TULIP,
	PINK,
	PEONY;

	/**
	 * Rotate our color cylinder by this many degrees
	 * to tweak which colors we actually get.
	 * A purely aesthetic choice.
	 */
	private static final double HUE_SALT = 0;

	public static Hue[] VALUES = Hue.values();
	public static int COUNT = VALUES.length;

	private int hueSample = 0;

	public double hueDegrees() {
		return ordinal() * 360.0 / Hue.values().length + HUE_SALT;
	}

	/**
	 * Initialized lazily because ordinal not available during instantiation.
	 * @return
	 */
	public int hueSample() {
		if (hueSample == 0) {
			hueSample = Color.fromHCL(hueDegrees(), Color.HCL_MAX, Color.HCL_MAX).ARGB | 0xFF000000;
		}

		return hueSample;
	}

	public String localizedName() {
		return I18n.get("color.hue." + name().toLowerCase(Locale.ROOT));
	}
}
