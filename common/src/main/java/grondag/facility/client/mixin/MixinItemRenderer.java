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

package grondag.facility.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.VertexConsumer;

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
