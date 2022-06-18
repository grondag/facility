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

package grondag.facility.client;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.inventory.InventoryMenu;

// TODO: remove and use FREX API instead
public class RendererHooks {
	public static final RenderType TRANSLUCENT = makeTranslucent();
	public static final RenderType CUTOUT = makeCutout();
	public static final RenderType FLUID = makeFluid();

	static class RenderSecrets extends RenderStateShard {
		RenderSecrets(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}

		static final TransparencyStateShard _NO_TRANSPARENCY = NO_TRANSPARENCY;
		static final TransparencyStateShard _TRANSLUCENT_TRANSPARENCY = TRANSLUCENT_TRANSPARENCY;
		//static final DiffuseLighting _ENABLE_DIFFUSE_LIGHTING = ENABLE_DIFFUSE_LIGHTING;
		//static final Alpha _ONE_TENTH_ALPHA = ONE_TENTH_ALPHA;
		//static final Alpha _ZERO_ALPHA = ZERO_ALPHA;
		static final LightmapStateShard _ENABLE_LIGHTMAP = LIGHTMAP;
		static final OverlayStateShard _ENABLE_OVERLAY_COLOR = OVERLAY;
		static final OverlayStateShard _DISABLE_OVERLAY_COLOR = NO_OVERLAY;
		static final LayeringStateShard ITEM_OFFSET_LAYERING = new RenderStateShard.LayeringStateShard("item_offset_layering", () -> {
			RenderSystem.polygonOffset(-1.0F, -1.0F);
			RenderSystem.enablePolygonOffset();
			//RenderSystem.disableRescaleNormal();
		}, () -> {
			RenderSystem.polygonOffset(0.0F, 0.0F);
			RenderSystem.disablePolygonOffset();
			//RenderSystem.enableRescaleNormal();
		});
	}

	private static RenderType makeCutout() {
		final RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
			.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityCutoutShader))
			.setTransparencyState(RenderSecrets._NO_TRANSPARENCY)
			.setLightmapState(RenderSecrets._ENABLE_LIGHTMAP)
			.setOverlayState(RenderSecrets._ENABLE_OVERLAY_COLOR)
			.setLayeringState(RenderSecrets.ITEM_OFFSET_LAYERING)
			.createCompositeState(true);
		return RenderType.create("entity_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, multiPhaseParameters);
	}

	private static RenderType makeTranslucent() {
		final RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
			.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader))
			.setTransparencyState(RenderSecrets._TRANSLUCENT_TRANSPARENCY)
			.setLightmapState(RenderSecrets._ENABLE_LIGHTMAP)
			.setOverlayState(RenderSecrets._ENABLE_OVERLAY_COLOR)
			.setLayeringState(RenderSecrets.ITEM_OFFSET_LAYERING)
			.createCompositeState(true);
		return RenderType.create("entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, multiPhaseParameters);
	}

	public static RenderType makeFluid() {
		final RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
			.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeSolidShader))
			.setTransparencyState(RenderSecrets._NO_TRANSPARENCY)
			.setLightmapState(RenderSecrets._ENABLE_LIGHTMAP)
			.setOverlayState(RenderSecrets._DISABLE_OVERLAY_COLOR)
			.setLayeringState(RenderSecrets.ITEM_OFFSET_LAYERING)
			.createCompositeState(true);
		return RenderType.create("fluid_overlay", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, false, multiPhaseParameters);
	}

	public static @Nullable RenderType hook(RenderType renderLayer) {
		return enabled ? renderLayer == Sheets.translucentCullBlockSheet() ? TRANSLUCENT : CUTOUT : null;
		//return enabled ? alpha < 255 || renderLayer == TexturedRenderLayers.getEntityTranslucent() ? TRANSLUCENT : CUTOUT : null;
	}

	public static MultiBufferSource wrap(MultiBufferSource wrapped) {
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

	private static MultiBufferSource wrappedProvider;
	private static VertexConsumer wrappedConsumer;

	private static final FadingVertexConsumer FADER = new FadingVertexConsumer();

	// Effect isn't great, so disabled for now
	@SuppressWarnings("unused")
	private static final MultiBufferSource FADE_PROVIDER = new MultiBufferSource() {
		@Override
		public VertexConsumer getBuffer(RenderType renderLayer) {
			if (renderLayer == TRANSLUCENT && alpha < 255) {
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
		public VertexConsumer uv(float f, float g) {
			wrappedConsumer.uv(f, g);
			return this;
		}

		@Override
		public VertexConsumer overlayCoords(int i, int j) {
			wrappedConsumer.overlayCoords(i, j);
			return this;
		}

		@Override
		public VertexConsumer uv2(int i, int j) {
			wrappedConsumer.uv2(i, j);
			return this;
		}

		@Override
		public VertexConsumer normal(float f, float g, float h) {
			wrappedConsumer.normal(f, g, h);
			return this;
		}

		@Override
		public void endVertex() {
			wrappedConsumer.endVertex();
		}

		@Override
		public void defaultColor(int i, int j, int k, int l) {
			wrappedConsumer.defaultColor(i, j, k, l);
		}

		@Override
		public void unsetDefaultColor() {
			wrappedConsumer.unsetDefaultColor();
		}
	}
}
