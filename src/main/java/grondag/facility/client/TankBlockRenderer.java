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

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.storage.bulk.TankBlockEntity;
import grondag.facility.storage.bulk.TankClientState;

@Environment(EnvType.CLIENT)
public class TankBlockRenderer extends StorageBlockRenderer<TankBlockEntity> {
	public TankBlockRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	@Override
	public void render(TankBlockEntity bin, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightmap, int overlay) {
		super.render(bin, tickDelta, matrixStack, vertexConsumerProvider, lightmap, overlay);
		renderInner(bin, tickDelta, matrixStack, RendererHooks.wrap(vertexConsumerProvider), lightmap, overlay);
	}

	protected void renderInner(TankBlockEntity be, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightmap, int overlay) {
		final TankClientState renderState = be.clientState();

		if(renderState.displayAlpha() == 0) {
			return;
		}

		final Sprite sprite = renderState.fluidSprite;

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

		final Matrix4f mat = matrixStack.peek().getModel();
		final Matrix3f nMat = matrixStack.peek().getNormal();

		final VertexConsumer vc = vertexConsumerProvider.getBuffer(RendererHooks.FLUID);

		final int color = renderState.fluidColor;
		final int r = (color >> 16) & 0xFF;
		final int g = (color >> 8) & 0xFF;
		final int b = color & 0xFF;
		final float yMax = 0.1f + renderState.level * 0.8f;
		final float vMax = sprite.getMinV() + (sprite.getMaxV() - sprite.getMinV()) * renderState.level;

		int light = renderState.glowing ? 0xF000F0 : WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().north());
		vc.vertex(mat, 0.9f, 0.1f, 0).color(r, g, b, 255).texture(sprite.getMinU(), sprite.getMinV()).light(light).normal(nMat, 0, 0, -1).next();
		vc.vertex(mat, 0.1f, 0.1f, 0).color(r, g, b, 255).texture(sprite.getMaxU(), sprite.getMinV()).light(light).normal(nMat, 0, 0, -1).next();
		vc.vertex(mat, 0.1f, yMax, 0).color(r, g, b, 255).texture(sprite.getMaxU(), vMax).light(light).normal(nMat, 0, 0, -1).next();
		vc.vertex(mat, 0.9f, yMax, 0).color(r, g, b, 255).texture(sprite.getMinU(), vMax).light(light).normal(nMat, 0, 0, -1).next();

		light = renderState.glowing ? 0xF000F0 : WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().south());
		vc.vertex(mat, 0.1f, 0.1f, 1).color(r, g, b, 255).texture(sprite.getMinU(), sprite.getMinV()).light(light).normal(nMat, 0, 0, 1).next();
		vc.vertex(mat, 0.9f, 0.1f, 1).color(r, g, b, 255).texture(sprite.getMaxU(), sprite.getMinV()).light(light).normal(nMat, 0, 0, 1).next();
		vc.vertex(mat, 0.9f, yMax, 1).color(r, g, b, 255).texture(sprite.getMaxU(), vMax).light(light).normal(nMat, 0, 0, 1).next();
		vc.vertex(mat, 0.1f, yMax, 1).color(r, g, b, 255).texture(sprite.getMinU(), vMax).light(light).normal(nMat, 0, 0, 1).next();

		light = renderState.glowing ? 0xF000F0 : WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().west());
		vc.vertex(mat, 0, 0.1f, 0.1f).color(r, g, b, 255).texture(sprite.getMinU(), sprite.getMinV()).light(light).normal(nMat, -1, 0, 0).next();
		vc.vertex(mat, 0, 0.1f, 0.9f).color(r, g, b, 255).texture(sprite.getMaxU(), sprite.getMinV()).light(light).normal(nMat, -1, 0, 0).next();
		vc.vertex(mat, 0, yMax, 0.9f).color(r, g, b, 255).texture(sprite.getMaxU(), vMax).light(light).normal(nMat, -1, 0, 0).next();
		vc.vertex(mat, 0, yMax, 0.1f).color(r, g, b, 255).texture(sprite.getMinU(), vMax).light(light).normal(nMat, -1, 0, 0).next();

		light = renderState.glowing ? 0xF000F0 : WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().east());
		vc.vertex(mat, 1, 0.1f, 0.9f).color(r, g, b, 255).texture(sprite.getMinU(), sprite.getMinV()).light(light).normal(nMat, 1, 0, 0).next();
		vc.vertex(mat, 1, 0.1f, 0.1f).color(r, g, b, 255).texture(sprite.getMaxU(), sprite.getMinV()).light(light).normal(nMat, 1, 0, 0).next();
		vc.vertex(mat, 1, yMax, 0.1f).color(r, g, b, 255).texture(sprite.getMaxU(), vMax).light(light).normal(nMat, 1, 0, 0).next();
		vc.vertex(mat, 1, yMax, 0.9f).color(r, g, b, 255).texture(sprite.getMinU(), vMax).light(light).normal(nMat, 1, 0, 0).next();
	}
}
