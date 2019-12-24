package grondag.contained.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;

import grondag.contained.client.ItemRendererHook;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
	@Inject(at = @At("HEAD"), method = "getArmorVertexConsumer", cancellable = true)
	private static void onGetArmorVertexConsumer(VertexConsumerProvider vertexConsumerProvider, RenderLayer renderLayer, boolean isWorld, boolean hasGlint, CallbackInfoReturnable<VertexConsumer> ci) {
		final RenderLayer newLayer = ItemRendererHook.hook(renderLayer);

		// FIXME: Glint causes problems so disable for now
		if (newLayer != null) {
			final VertexConsumer buffer = vertexConsumerProvider.getBuffer(newLayer);
			ci.setReturnValue(buffer);
			//			ci.setReturnValue(hasGlint ? VertexConsumers.dual(vertexConsumerProvider.getBuffer(RenderLayer.getEntityGlint()), buffer) : buffer);
		}
	}
}