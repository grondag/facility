package grondag.facility.varia;

import grondag.fermion.color.ColorUtil;

public class FacilityColors {
	public static final int[] BASE = new int[16];
	public static final int[] HIGHLIGHT = new int[16];
	public static final int[] GLOW = new int[16];

	static {
		final float arc = 360f / 16f;

		int mod = 0;

		for (int i = 0; i < 16; ++i) {
			final float hue = (mod % 16) * arc;
			HIGHLIGHT[i] = ColorUtil.hclToSrgb(hue, 20, 50);
			BASE[i] = 0x80000000 | (ColorUtil.hclToSrgb(hue + 2, 10, 35) & 0xFFFFFF);

			int l = 90;
			int glow = ColorUtil.hclToSrgb(hue, 20, l);

			while (glow == ColorUtil.NO_COLOR) {
				glow = ColorUtil.hclToSrgb(hue, 20, --l);
			}

			GLOW[i] = glow;

			mod += 7;
		}
	}
}
