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

public class ColorUtil {
	private static final double K = 903.3;
	private static final double E = 0.008856;

	//NB: using standard illuminant E and outputting linear RGB - color correction to be applied in rendering
	private static final double D65X = 0.95047;
	private static final double D65Y = 1.00000;
	private static final double D65Z = 1.08883;
	public static final int NO_COLOR = 0;

	public static int labToSrgb(double l, double a, double b) {
		final double y0 = (l + 16) / 116;
		final double z0 = y0 - b / 200;
		final double x0 = a / 500 + y0;

		final double x3 = x0 * x0 * x0;
		final double x1 = x3 > E ? x3 : (116 * x0 - 16) / K;

		final double y1 = l > K * E ? y0 * y0 * y0 : l / K;

		final double z3 = z0 * z0 * z0;

		final double z1 = z3 > E ? z3 : (116 * z0 - 16) / K;

		return xyzToSrgb(x1 * D65X, y1 * D65Y, z1 * D65Z);
	}

	public static int hclToSrgb(double hue, double chroma, double luminance) {
		return labToSrgb(luminance, Math.cos(Math.toRadians(hue)) * chroma, Math.sin(Math.toRadians(hue)) * chroma);
	}

	/**
	 * If color is visible, alpha btye = 255.
	 */
	public static int xyzToSrgb(double x, double y, double z) {
		if (!(x >= 0 && x <= D65X && y >= 0 && y <= D65Y && z >= 0 && z <= D65Z)) {
			return NO_COLOR;
		} else {
			double r = x * 3.24096994 + y * -1.53738318 + z * -0.49861076;
			double g = x * -0.96924364 + y * 1.87596750 + z * 0.04155506;
			double b = x * 0.05563008 + y * -0.20397696 + z * 1.05697151;

			/**
			 * SRGB tops out slightly - slightly less than pure white, at 0xFFF7F7F7;
			 * We are usually going to multiply this by texture pixel colors that
			 * expect no vertex color = purse white, so we don't subtract as much here
			 * as the standard normally calls for.
			 */
			r = r <= 0.0031308 ? 12.92 * r : Math.pow(1.055 * r, 1/2.4) - 0.0255; //- 0.055;
			g = g <= 0.0031308 ? 12.92 * g : Math.pow(1.055 * g, 1/2.4) - 0.0255; //- 0.055;
			b = b <= 0.0031308 ? 12.92 * b : Math.pow(1.055 * b, 1/2.4) - 0.0255; //- 0.055;

			final int red = (int) Math.round(r * 255);
			final int green = (int) Math.round(g * 255);
			final int blue = (int) Math.round(b * 255);

			if ((red & 0xFF) == red && (green & 0xFF) == green && (blue & 0xFF) == blue) {
				return 0xFF000000 | (red << 16) | (green << 8) | blue;
			} else {
				return NO_COLOR;
			}
		}
	}

	public static int hcToSrgbGlow(double hue, double chroma) {
		int l = 100;
		int result = ColorUtil.hclToSrgb(hue, chroma, l);

		while (result == ColorUtil.NO_COLOR) {
			result = ColorUtil.hclToSrgb(hue, chroma, --l);
		}

		return result;
	}
}
