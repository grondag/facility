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

package grondag.facility.init;

import static grondag.xm.api.texture.TextureGroup.STATIC_TILES;
import static grondag.xm.api.texture.TextureRenderIntent.BASE_ONLY;
import static grondag.xm.api.texture.TextureScale.SINGLE;
import static grondag.xm.api.texture.TextureTransform.IDENTITY;
import static grondag.xm.api.texture.TextureTransform.ROTATE_RANDOM;
import static grondag.xm.api.texture.TextureTransform.STONE_LIKE;

import grondag.facility.Facility;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.paint.XmPaintFinder;
import grondag.xm.api.texture.TextureLayoutMap;
import grondag.xm.api.texture.TextureSet;
import grondag.xm.api.texture.core.CoreTextures;
import grondag.xm.texture.TextureSetHelper;

public abstract class Textures {
	private Textures() { }

	public static final TextureSet CRATE_BASE = TextureSet.builder()
			.displayNameToken("crate_base").baseTextureName("facility:block/crate_base")
			.versionCount(4).scale(SINGLE).layout(TextureLayoutMap.VERSIONED).transform(STONE_LIKE)
			.renderIntent(BASE_ONLY).groups(STATIC_TILES).build("contained:crate_base");

	public static final TextureSet OPEN_BOX = TextureSetHelper.addDecal(Facility.MODID, "open_box", "open_box", ROTATE_RANDOM);
	public static final TextureSet FILLED_BOX = TextureSetHelper.addDecal(Facility.MODID, "filled_box", "filled_box", ROTATE_RANDOM);
	public static final TextureSet BIN_FACE = TextureSetHelper.addDecal(Facility.MODID, "bin_face", "bin_face", IDENTITY);
	public static final TextureSet HALF_DIVIDER = TextureSetHelper.addDecal(Facility.MODID, "half_divider", "half_divider", STONE_LIKE);
	public static final TextureSet QUARTER_DIVIDER = TextureSetHelper.addDecal(Facility.MODID, "quarter_divider", "quarter_divider", STONE_LIKE);
	public static XmPaint cratePaintWithDecal(TextureSet decal, int color) {
		return Textures.crateBaseFinder(3)
				.texture(2, decal)
				.textureColor(2, color)
				.find();
	}

	public static XmPaintFinder crateBaseFinder(int depth) {
		return XmPaint.finder()
				.textureDepth(depth)
				.texture(0, CRATE_BASE)
				.textureColor(0, 0xFFFFFFFF)
				.texture(1, CoreTextures.BORDER_WEATHERED_LINE)
				.textureColor(1, 0xA0000000);
	}

	public static void initialize() {
		// NOOP - forces static initialization
	}
}
