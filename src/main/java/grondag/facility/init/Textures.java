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
package grondag.facility.init;

import static grondag.xm.api.texture.TextureGroup.STATIC_TILES;
import static grondag.xm.api.texture.TextureRenderIntent.BASE_ONLY;
import static grondag.xm.api.texture.TextureScale.SINGLE;
import static grondag.xm.api.texture.TextureTransform.IDENTITY;
import static grondag.xm.api.texture.TextureTransform.ROTATE_RANDOM;
import static grondag.xm.api.texture.TextureTransform.STONE_LIKE;

import grondag.facility.Facility;
import grondag.xm.api.paint.PaintBlendMode;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.paint.XmPaintFinder;
import grondag.xm.api.texture.TextureLayoutMap;
import grondag.xm.api.texture.TextureSet;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.texture.TextureSetHelper;

public enum Textures { ;

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
				.blendMode(2, PaintBlendMode.TRANSLUCENT)
				.textureColor(2, color)
				.find();
	}
	public static XmPaintFinder crateBaseFinder(int depth) {
		return XmPaint.finder()
				.textureDepth(depth)
				.texture(0, CRATE_BASE)
				.textureColor(0, 0xFFFFFFFF)
				.texture(1, XmTextures.BORDER_WEATHERED_LINE)
				.blendMode(1, PaintBlendMode.TRANSLUCENT)
				.textureColor(1, 0xA0000000);
	}

}
