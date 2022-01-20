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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import grondag.facility.storage.bulk.TankBlockEntity;
import grondag.facility.storage.bulk.TankClientState;

@Environment(EnvType.CLIENT)
public class TankBlockRenderer extends StorageBlockRenderer<TankBlockEntity> {
	public TankBlockRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(TankBlockEntity bin, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int lightmap, int overlay) {
		super.render(bin, tickDelta, matrixStack, vertexConsumerProvider, lightmap, overlay);
		renderInner(bin, tickDelta, matrixStack, RendererHooks.wrap(vertexConsumerProvider), lightmap, overlay);
	}

	protected void renderInner(TankBlockEntity be, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int lightmap, int overlay) {
		final TankClientState renderState = be.clientState();

		if(renderState.displayAlpha() == 0) {
			return;
		}

		final TextureAtlasSprite sprite = renderState.fluidSprite;

		if(sprite == null) {
			return;
		}

		// PERF - handle face occlusion
		//		final Direction face = bin.getCachedState().get(XmProperties.FACE);
		//		final World world = bin.getWorld();
		//		final BlockPos occludingPos = bin.getPos().offset(face.getOpposite());
		//		final BlockState occludingState = world.getBlockState(occludingPos);
		//
		//		if(occludingState.isFullOpaque(world, occludingPos)) {
		//			return;
		//		}

		final Matrix4f mat = matrixStack.last().pose();
		final Matrix3f nMat = matrixStack.last().normal();

		final VertexConsumer vc = vertexConsumerProvider.getBuffer(RendererHooks.FLUID);

		final int color = renderState.fluidColor;
		final int r = (color >> 16) & 0xFF;
		final int g = (color >> 8) & 0xFF;
		final int b = color & 0xFF;
		final float yMax = 0.1f + renderState.level * 0.8f;
		final float vMax = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * renderState.level;

		int light = renderState.glowing ? 0xF000F0 : LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().north());
		vc.vertex(mat, 0.9f, 0.1f, 0).color(r, g, b, 255).uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(nMat, 0, 0, -1).endVertex();
		vc.vertex(mat, 0.1f, 0.1f, 0).color(r, g, b, 255).uv(sprite.getU1(), sprite.getV0()).uv2(light).normal(nMat, 0, 0, -1).endVertex();
		vc.vertex(mat, 0.1f, yMax, 0).color(r, g, b, 255).uv(sprite.getU1(), vMax).uv2(light).normal(nMat, 0, 0, -1).endVertex();
		vc.vertex(mat, 0.9f, yMax, 0).color(r, g, b, 255).uv(sprite.getU0(), vMax).uv2(light).normal(nMat, 0, 0, -1).endVertex();

		light = renderState.glowing ? 0xF000F0 : LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().south());
		vc.vertex(mat, 0.1f, 0.1f, 1).color(r, g, b, 255).uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(nMat, 0, 0, 1).endVertex();
		vc.vertex(mat, 0.9f, 0.1f, 1).color(r, g, b, 255).uv(sprite.getU1(), sprite.getV0()).uv2(light).normal(nMat, 0, 0, 1).endVertex();
		vc.vertex(mat, 0.9f, yMax, 1).color(r, g, b, 255).uv(sprite.getU1(), vMax).uv2(light).normal(nMat, 0, 0, 1).endVertex();
		vc.vertex(mat, 0.1f, yMax, 1).color(r, g, b, 255).uv(sprite.getU0(), vMax).uv2(light).normal(nMat, 0, 0, 1).endVertex();

		light = renderState.glowing ? 0xF000F0 : LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().west());
		vc.vertex(mat, 0, 0.1f, 0.1f).color(r, g, b, 255).uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(nMat, -1, 0, 0).endVertex();
		vc.vertex(mat, 0, 0.1f, 0.9f).color(r, g, b, 255).uv(sprite.getU1(), sprite.getV0()).uv2(light).normal(nMat, -1, 0, 0).endVertex();
		vc.vertex(mat, 0, yMax, 0.9f).color(r, g, b, 255).uv(sprite.getU1(), vMax).uv2(light).normal(nMat, -1, 0, 0).endVertex();
		vc.vertex(mat, 0, yMax, 0.1f).color(r, g, b, 255).uv(sprite.getU0(), vMax).uv2(light).normal(nMat, -1, 0, 0).endVertex();

		light = renderState.glowing ? 0xF000F0 : LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().east());
		vc.vertex(mat, 1, 0.1f, 0.9f).color(r, g, b, 255).uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(nMat, 1, 0, 0).endVertex();
		vc.vertex(mat, 1, 0.1f, 0.1f).color(r, g, b, 255).uv(sprite.getU1(), sprite.getV0()).uv2(light).normal(nMat, 1, 0, 0).endVertex();
		vc.vertex(mat, 1, yMax, 0.1f).color(r, g, b, 255).uv(sprite.getU1(), vMax).uv2(light).normal(nMat, 1, 0, 0).endVertex();
		vc.vertex(mat, 1, yMax, 0.9f).color(r, g, b, 255).uv(sprite.getU0(), vMax).uv2(light).normal(nMat, 1, 0, 0).endVertex();
	}
}
