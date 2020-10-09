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
package grondag.facility.client;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;

// TODO: remove and use AW instead
public class RendererHooks {
	public static final RenderLayer TRANSLUCENT = makeTranslucent();
	public static final RenderLayer CUTOUT = makeCutout();
	public static final RenderLayer FLUID = makeFluid();

	static class RenderSecrets extends RenderPhase {
		public RenderSecrets(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}

		static final Transparency _NO_TRANSPARENCY = NO_TRANSPARENCY;
		static final Transparency _TRANSLUCENT_TRANSPARENCY = TRANSLUCENT_TRANSPARENCY;
		static final DiffuseLighting _ENABLE_DIFFUSE_LIGHTING = ENABLE_DIFFUSE_LIGHTING;
		static final Alpha _ONE_TENTH_ALPHA = ONE_TENTH_ALPHA;
		static final Alpha _ZERO_ALPHA = ZERO_ALPHA;
		static final Lightmap _ENABLE_LIGHTMAP = ENABLE_LIGHTMAP;
		static final Overlay _ENABLE_OVERLAY_COLOR = ENABLE_OVERLAY_COLOR;
		static final Overlay _DISABLE_OVERLAY_COLOR = DISABLE_OVERLAY_COLOR;
		static final Layering ITEM_OFFSET_LAYERING = new RenderPhase.Layering("item_offset_layering", () -> {
			RenderSystem.polygonOffset(-1.0F, -1.0F);
			RenderSystem.enablePolygonOffset();
			RenderSystem.disableRescaleNormal();
		}, () -> {
			RenderSystem.polygonOffset(0.0F, 0.0F);
			RenderSystem.disablePolygonOffset();
			RenderSystem.enableRescaleNormal();
		});
	}

	private static RenderLayer makeCutout() {
		final RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
		.texture(new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false))
		.transparency(RenderSecrets._NO_TRANSPARENCY)
		.diffuseLighting(RenderSecrets._ENABLE_DIFFUSE_LIGHTING)
		.alpha(RenderSecrets._ONE_TENTH_ALPHA)
		.lightmap(RenderSecrets._ENABLE_LIGHTMAP)
		.overlay(RenderSecrets._ENABLE_OVERLAY_COLOR)
		.layering(RenderSecrets.ITEM_OFFSET_LAYERING)
		.build(true);
		return RenderLayer.of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseParameters);
	}

	private static RenderLayer makeTranslucent() {
		final RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
		.texture(new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false))
		.transparency(RenderSecrets._TRANSLUCENT_TRANSPARENCY)
		.diffuseLighting(RenderSecrets._ENABLE_DIFFUSE_LIGHTING)
		.alpha(RenderSecrets._ONE_TENTH_ALPHA)
		.lightmap(RenderSecrets._ENABLE_LIGHTMAP)
		.overlay(RenderSecrets._ENABLE_OVERLAY_COLOR)
		.layering(RenderSecrets.ITEM_OFFSET_LAYERING)
		.build(true);
		return RenderLayer.of("entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, multiPhaseParameters);
	}

	public static RenderLayer makeFluid() {
		final RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
		.texture(new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false))
		.transparency(RenderSecrets._NO_TRANSPARENCY)
		.diffuseLighting(RenderSecrets._ENABLE_DIFFUSE_LIGHTING)
		.alpha(RenderSecrets._ZERO_ALPHA)
		.lightmap(RenderSecrets._ENABLE_LIGHTMAP)
		.overlay(RenderSecrets._DISABLE_OVERLAY_COLOR)
		.layering(RenderSecrets.ITEM_OFFSET_LAYERING)
		.build(true);
		return RenderLayer.of("fluid_overlay", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 256, false, false, multiPhaseParameters);
	}

	public static @Nullable RenderLayer hook(RenderLayer renderLayer) {
		return enabled ? renderLayer == TexturedRenderLayers.getEntityTranslucentCull() ? TRANSLUCENT : CUTOUT : null;
		//return enabled ? alpha < 255 || renderLayer == TexturedRenderLayers.getEntityTranslucent() ? TRANSLUCENT : CUTOUT : null;
	}

	public static VertexConsumerProvider wrap(VertexConsumerProvider wrapped) {
		//		if(alpha < 255) {
		//			wrappedProvider = wrapped;
		//			return FADE_PROVIDER;
		//		} else {
		return wrapped;
		//		}
	}

	static boolean enabled = false;
	static int alpha = 255;

	public static void enable(float alphaIn) {
		enabled = true;
		alpha = (int) (alphaIn * 255);
	}

	public static void disable() {
		enabled = false;
	}

	private static VertexConsumerProvider wrappedProvider;
	private static VertexConsumer wrappedConsumer;

	private static final FadingVertexConsumer FADER = new FadingVertexConsumer();

	// Effect isn't great, so disabled for now
	@SuppressWarnings("unused")
	private static final VertexConsumerProvider FADE_PROVIDER = new VertexConsumerProvider() {
		@Override
		public VertexConsumer getBuffer(RenderLayer renderLayer) {
			if(renderLayer == TRANSLUCENT && alpha < 255) {
				wrappedConsumer = wrappedProvider.getBuffer(TRANSLUCENT);
				//System.out.println(alpha);
				return FADER;
			} else {
				return wrappedProvider.getBuffer(renderLayer);
			}
		}
	};

	private static class FadingVertexConsumer implements VertexConsumer {
		@Override
		public VertexConsumer vertex(double d, double e, double f) {
			wrappedConsumer.vertex(d, e, f);
			return this;
		}

		@Override
		public VertexConsumer color(int r, int g, int b, int a) {
			wrappedConsumer.color(r, g, b, alpha);
			return this;
		}

		@Override
		public VertexConsumer texture(float f, float g) {
			wrappedConsumer.texture(f, g);
			return this;
		}

		@Override
		public VertexConsumer overlay(int i, int j) {
			wrappedConsumer.overlay(i, j);
			return this;
		}

		@Override
		public VertexConsumer light(int i, int j) {
			wrappedConsumer.light(i, j);
			return this;
		}

		@Override
		public VertexConsumer normal(float f, float g, float h) {
			wrappedConsumer.normal(f, g, h);
			return null;
		}

		@Override
		public void next() {
			wrappedConsumer.next();
		}
	}
}
