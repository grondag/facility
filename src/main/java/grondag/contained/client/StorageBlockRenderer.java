package grondag.contained.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.contained.block.ItemStorageBlockEntity;

@Environment(EnvType.CLIENT)
public class StorageBlockRenderer<T extends ItemStorageBlockEntity> extends BlockEntityRenderer<T> {
	protected final MinecraftClient mc = MinecraftClient.getInstance();
	protected final ItemRenderer ir = mc.getItemRenderer();

	public StorageBlockRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	@Override
	public void render(T blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {

	}
}
