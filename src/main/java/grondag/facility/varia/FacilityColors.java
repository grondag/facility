package grondag.facility.varia;

import grondag.fermion.color.ColorUtil;

public class FacilityColors {
	public static final int[] BASE = new int[16];
	public static final int[] HIGHLIGHT = new int[16];
	public static final int[] GLOW = new int[16];

	static {
		init();
	}

	public static void init() {
		final int warmWhiteHue = 73;
		BASE[0] = 0x80000000 | (ColorUtil.hclToSrgb(warmWhiteHue, 2, 35) & 0xFFFFFF);
		HIGHLIGHT[0] = ColorUtil.hclToSrgb(warmWhiteHue, 2, 50);
		GLOW[0] = ColorUtil.hcToSrgbGlow(warmWhiteHue, 2);

		final int uvHue = 300;
		BASE[8] = 0x80000000 | (ColorUtil.hclToSrgb(uvHue, 40, 40) & 0xFFFFFF);
		HIGHLIGHT[8] = ColorUtil.hclToSrgb(uvHue, 60, 60);
		GLOW[8] = ColorUtil.hcToSrgbGlow(uvHue, 60);

		final float arc = 360f / 7f;
		final float aStartHue = 210;
		final float bStartHue = aStartHue + arc * 0.5f;

		for (int i = 0; i < 7; ++i) {
			final float hueA = aStartHue + arc * i;
			final int a = i  + 1;
			HIGHLIGHT[a] = ColorUtil.hclToSrgb(hueA, 20, 50);
			BASE[a] = 0x80000000 | (ColorUtil.hclToSrgb(hueA + 2, 10, 35) & 0xFFFFFF);
			GLOW[a] =ColorUtil.hcToSrgbGlow(hueA, 20);

			final float hueB = bStartHue + arc * i;
			final int b = i  + 9;
			HIGHLIGHT[b] = ColorUtil.hclToSrgb(hueB, 30, 50);
			BASE[b] = 0x80000000 | (ColorUtil.hclToSrgb(hueB + 2, 15, 35) & 0xFFFFFF);
			GLOW[b] =ColorUtil.hcToSrgbGlow(hueB, 30);
		}
	}
}
