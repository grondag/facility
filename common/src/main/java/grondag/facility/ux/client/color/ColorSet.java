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

import net.minecraft.client.resources.language.I18n;

import grondag.facility.ux.client.color.Color.HCLMode;

/**
 * @deprecated  use ColorUtil
 */
@Deprecated
public class ColorSet {
	public final int ordinal;
	public final Hue hue;
	public final Chroma chroma;
	public final Luminance luminance;

	private final int[] colors = new int[Tone.values().length];

	public ColorSet(Hue hue, Chroma chromaIn, Luminance luminanceIn, int ordinal) {
		this.ordinal = ordinal;
		this.hue = hue;
		chroma = chromaIn;
		luminance = luminanceIn;
	}

	public ColorSet setColor(Tone whichColor, int colorValue) {
		colors[whichColor.ordinal()] = colorValue;
		return this;
	}

	public int getColor(Tone whichColor) {
		return colors[whichColor.ordinal()];
	}

	public String localizedName() {
		final String format = I18n.get(chroma == Chroma.PURE_NETURAL ? "color.format.pure_neutral" : "color.format.color");
		return String.format(format, hue.localizedName(), chroma.localizedName(), luminance.localizedName());
	}

	public static ColorSet makeColorMap(Hue hue, Chroma chromaIn, Luminance luminanceIn, int ordinal) {
		final ColorSet newColorMap = new ColorSet(hue, chromaIn, luminanceIn, ordinal);

		// use these for manipulation so can use realistic values for HCL_MAX inputs
		final double chroma = chromaIn.value;
		final double luminance = luminanceIn.value;

		final Color baseColor = Color.fromHCL(hue.hueDegrees(), chroma, luminance, HCLMode.REDUCE_CHROMA);

		newColorMap.setColor(Tone.BASE, baseColor.ARGB | 0xFF000000);

		// BORDERS
		final Color whichColor = Color.fromHCL(hue.hueDegrees() + 15,
				chroma < 10 ? chroma + 10 : chroma * 0.5,
						luminance < 60 ? luminance + 15 : luminance - 15,
								HCLMode.REDUCE_CHROMA);
		assert whichColor.IS_VISIBLE : "makeColorMap produced invisible border color for " + newColorMap.localizedName();

		newColorMap.setColor(Tone.BORDER, whichColor.ARGB | 0xFF000000);

		//        newColorMap.setColor(EnumColorMap.HIGHLIGHT,
		//                NiceHues.INSTANCE.getHueSet(hue).getColorSetForHue(HuePosition.OPPOSITE).getColor(tint) | 0xFF000000);

		final Color lampColor = Color.fromHCL(hue.hueDegrees(), baseColor.HCL_C, Color.HCL_MAX, HCLMode.NORMAL);

		assert lampColor.ARGB != 0 : "Bad color hcl" + hue.hueDegrees() + " " + chromaIn.value / 2 + " " + Color.HCL_MAX;

		newColorMap.setColor(Tone.LAMP, lampColor.ARGB | 0xFF000000);

		return newColorMap;
	}

	public enum Tone {
		BASE,
		HIGHLIGHT,
		BORDER,
		LAMP
	}
}
