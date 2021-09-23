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
package grondag.facility.client.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.client.RendererHooks;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
	@Inject(at = @At("HEAD"), method = "getArmorFoilBuffer", cancellable = true)
	private static void onGetArmorFoilBuffer(MultiBufferSource vertexConsumerProvider, RenderType renderLayer, boolean isWorld, boolean hasGlint, CallbackInfoReturnable<VertexConsumer> ci) {
		final RenderType newLayer = RendererHooks.hook(renderLayer);

		// FIXME: Glint causes problems so disable for now
		if (newLayer != null) {
			final VertexConsumer buffer = vertexConsumerProvider.getBuffer(newLayer);
			ci.setReturnValue(buffer);
			//			ci.setReturnValue(hasGlint ? VertexConsumers.dual(vertexConsumerProvider.getBuffer(RenderLayer.getEntityGlint()), buffer) : buffer);
		}
	}
}
