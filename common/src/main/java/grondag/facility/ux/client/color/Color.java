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

import net.minecraft.util.Mth;

/**
 * Unique library for color conversion and manipulation. Less complicated than
 * java.awt.color and has conversions it seems to lack.
 *
 * <p>Hat tip to http://easyrgb.com for their reference page on conversion
 * formulas.
 *
 * @deprecated  use ColorUtil
 */
@Deprecated
public class Color {
	public static final int HCL_MAX = 999;

	public final int ARGB;
	public final int red;
	public final int green;
	public final int blue;

	public final float HSV_H;
	public final float HSV_S;
	public final float HSV_V;

	public final float XYZ_X;
	public final float XYZ_Y;
	public final float XYZ_Z;

	public final float HCL_H;
	public final float HCL_C;
	public final float HCL_L;

	public final float LAB_L;
	public final float LAB_A;
	public final float LAB_B;

	/** Can this be displayed on an sRGB display? */
	public final boolean IS_VISIBLE;

	public static final int WHITE = 0xFFFFFFFF;
	public static final int BLACK = 0xFF000000;

	/**
	 * CIE D65 noon daylight standard illuminant - appropriate for sRBG color space.
	 */
	private static final float D65X = (float) 95.047;
	private static final float D65Y = (float) 100.000;
	private static final float D65Z = (float) 108.833;

	public static Color fromLab(double l, double a, double b) {
		final double y0 = (l + 16) / 116;
		final double x0 = a / 500 + y0;
		final double z0 = y0 - b / 200;

		final double y1 = (Math.pow(y0, 3) > 0.008856) ? Math.pow(y0, 3) : (y0 - 16 / 116) / 7.787;
		final double x1 = (Math.pow(y0, 3) > 0.008856) ? Math.pow(x0, 3) : (x0 - 16 / 116) / 7.787;
		final double z1 = (Math.pow(y0, 3) > 0.008856) ? Math.pow(z0, 3) : (z0 - 16 / 116) / 7.787;

		return new Color(x1 * D65X, y1 * D65Y, z1 * D65Z);
	}

	public static Color fromHCL(double hue, double chroma, double luminance) {
		return fromHCL(hue, chroma, luminance, HCLMode.NORMAL);
	}

	public static Color fromHCL(Hue hue, Chroma chroma, Luminance luminance) {
		return fromHCL(hue.hueDegrees(), chroma.value, luminance.value, HCLMode.REDUCE_CHROMA);
	}

	public static Color fromHCL(double hue, double chroma, double luminance, HCLMode failureMode) {
		// if both are max then make as saturated as possible, then find max lightness
		if (luminance == HCL_MAX && chroma == HCL_MAX) {
			//            double maxLuminance = 0;
			double maxChroma = 0;

			for (double l = 100; l > 20; l--) {
				for (double c = 100; c > 30; c--) {
					final Color temp = fromHCLSimple(hue, c, l);

					if (temp.IS_VISIBLE) {
						//                                 maxLuminance = Math.max(maxLuminance, l);
						maxChroma = Math.max(maxChroma, c);
					}
				}
			}

			//            if(maxLuminance > maxChroma)
			//            {
			//                luminance = maxLuminance;
			//                chroma = MAX;
			//            }
			//            else
			//            {
			chroma = maxChroma;
			luminance = HCL_MAX;
			//            }
		}

		if (luminance == HCL_MAX) {
			for (double trial = 100; trial > 0; trial -= 0.1) {
				final Color temp = fromHCLSimple(hue, chroma, trial);

				if (temp.IS_VISIBLE) {
					luminance = trial;
					break;
				}
			}
		}

		if (chroma == HCL_MAX) {
			for (double trial = 100; trial > 0; trial -= 0.1) {
				final Color temp = fromHCLSimple(hue, trial, luminance);

				if (temp.IS_VISIBLE) {
					chroma = trial;
					break;
				}
			}
		}

		Color testColor = fromHCLSimple(hue, chroma, luminance);

		if (!testColor.IS_VISIBLE && failureMode == HCLMode.REDUCE_CHROMA) {
			while (!testColor.IS_VISIBLE && chroma > 1) {
				chroma--;
				testColor = fromHCLSimple(hue, chroma, luminance);
			}
		}

		return fromHCLSimple(hue, chroma, luminance);
	}

	private static Color fromHCLSimple(double hue, double chroma, double luminance) {
		return fromLab(luminance, Math.cos(Math.toRadians(hue)) * chroma, Math.sin(Math.toRadians(hue)) * chroma);
	}

	public static Color fromRGB(int r, int g, int b) {
		final double r0 = r / 255.0;
		final double g0 = g / 255.0;
		final double b0 = b / 255.0;

		final double r1 = ((r0 > 0.04045) ? Math.pow((r0 + 0.055) / 1.055, 2.4) : r0 / 12.92) * 100;
		final double g1 = ((g0 > 0.04045) ? Math.pow((g0 + 0.055) / 1.055, 2.4) : g0 / 12.92) * 100;
		final double b1 = ((b0 > 0.04045) ? Math.pow((b0 + 0.055) / 1.055, 2.4) : b0 / 12.92) * 100;

		final double x = r1 * 0.4124 + g1 * 0.3576 + b1 * 0.1805;
		final double y = r1 * 0.2126 + g1 * 0.7152 + b1 * 0.0722;
		final double z = r1 * 0.0193 + g1 * 0.1192 + b1 * 0.9505;

		return new Color(x, y, z);
	}

	public static Color fromRGB(int rgb) {
		return fromRGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}

	protected Color(double x, double y, double z) {
		XYZ_X = (float) x;
		XYZ_Y = (float) y;
		XYZ_Z = (float) z;

		if (!(x >= 0 && x <= D65X && y >= 0 && y <= D65Y && z >= 0 && z <= D65Z)) {
			red = 0;
			green = 0;
			blue = 0;
			ARGB = 0;
			IS_VISIBLE = false;
		} else {
			// Convert to sRGB
			final double x0 = x / 100;
			final double y0 = y / 100;
			final double z0 = z / 100;

			final double r0 = x0 * 3.2406 + y0 * -1.5372 + z0 * -0.4986;
			final double g0 = x0 * -0.9689 + y0 * 1.8758 + z0 * 0.0415;
			final double b0 = x0 * 0.0557 + y0 * -0.2040 + z0 * 1.0570;

			final double r1 = (r0 > 0.0031308) ? (1.055 * Math.pow(r0, 1 / 2.4) - 0.055) : 12.92 * r0;
			final double g1 = (g0 > 0.0031308) ? (1.055 * Math.pow(g0, 1 / 2.4) - 0.055) : 12.92 * g0;
			final double b1 = (b0 > 0.0031308) ? (1.055 * Math.pow(b0, 1 / 2.4) - 0.055) : 12.92 * b0;

			if (r1 >= -0.000001 && r1 <= 1.000001 && g1 >= -0.000001 && g1 <= 1.000001 && b1 >= -0.000001
					&& b1 <= 1.000001) {
				red = (int) Math.round(r1 * 255);
				green = (int) Math.round(g1 * 255);
				blue = (int) Math.round(b1 * 255);
				ARGB = 0xFF000000 | (red << 16) | (green << 8) | blue;
				IS_VISIBLE = true;
			} else {
				red = 0;
				green = 0;
				blue = 0;
				ARGB = 0;
				IS_VISIBLE = false;
			}
		}

		// Convert to HSV
		{
			if (!IS_VISIBLE) {
				HSV_H = 0;
				HSV_S = 0;
				HSV_V = 0;
			} else {
				final double r = red / 255;
				final double g = green / 255;
				final double b = blue / 255;

				final double min = Math.min(Math.min(r, g), b);
				final double max = Math.max(Math.max(r, g), b);
				final double delta = max - min;

				HSV_V = (float) max;

				if (delta == 0) {
					// grey
					HSV_H = 0;
					HSV_S = 0;
				} else {
					HSV_S = (float) (delta / max);

					final double delta_r = (((max - r) / 6) + (max / 2)) / max;
					final double delta_g = (((max - g) / 6) + (max / 2)) / max;
					final double delta_b = (((max - b) / 6) + (max / 2)) / max;

					double h_temp;

					if (r == max) {
						h_temp = delta_b - delta_g;
					} else if (g == max) {
						h_temp = (1 / 3) + delta_r - delta_b;
					} else {
						// implies b == max
						h_temp = (2 / 3) + delta_g - delta_r;
					}

					if (h_temp < 0) {
						h_temp += 1;
					}

					if (h_temp > 1) {
						h_temp -= 1;
					}

					HSV_H = (float) h_temp;
				}
			}
		}

		// Convert to L*a*b
		{
			final double x0 = x / D65X;
			final double y0 = y / D65Y;
			final double z0 = z / D65Z;

			final double x1 = (x0 > 0.008856) ? Math.cbrt(x0) : 7.787 * x0 + (16 / 116);
			final double y1 = (y0 > 0.008856) ? Math.cbrt(y0) : 7.787 * y0 + (16 / 116);
			final double z1 = (z0 > 0.008856) ? Math.cbrt(z0) : 7.787 * z0 + (16 / 116);

			LAB_L = (float) ((116 * y1) - 16);
			LAB_A = (float) (500 * (x1 - y1));
			LAB_B = (float) (200 * (y1 - z1));
		}

		// Convert to HCL
		{
			final double h0 = Math.atan2(LAB_B, LAB_A);
			HCL_H = (float) ((h0 > 0) ? ((h0 / Math.PI) * 180) : 360 - (Math.abs(h0) / Math.PI) * 180);

			HCL_L = LAB_L;
			HCL_C = (float) Math.sqrt(Math.pow(LAB_A, 2) + Math.pow(LAB_B, 2));
		}
	}

	/**
	 * Makes dark colors lighter, or if negative number input will darken Range is
	 * -100 to +100, but typical values much more narrow.
	 */
	public Color lighten(int howMuch) {
		final float lightness = Mth.clamp(HCL_L + howMuch, 0, 100);
		return fromHCL(HCL_H, HCL_C, lightness, HCLMode.REDUCE_CHROMA);
	}

	/**
	 * Makes color as light as possible without reducing chroma - may return self if
	 * already at max luminance.
	 */
	public Color lumify() {
		return Color.fromHCL(HCL_H, HCL_C, Color.HCL_MAX, HCLMode.NORMAL);
	}

	/**
	 * Makes colors more saturated, or if negative number input desaturated Range is
	 * -100 to +100, but typical values much more narrow.
	 */
	public Color saturate(int howMuch) {
		final float chroma = Mth.clamp(HCL_C + howMuch, 0, 100);
		return fromHCL(HCL_H, chroma, HCL_L, HCLMode.REDUCE_CHROMA);
	}

	public enum HCLMode {
		NORMAL, REDUCE_CHROMA;
	}
}
