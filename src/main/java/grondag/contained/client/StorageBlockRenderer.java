package grondag.contained.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.contained.block.ItemStorageBlockEntity;

@Environment(EnvType.CLIENT)
public class StorageBlockRenderer extends BlockEntityRenderer<ItemStorageBlockEntity> {

	public StorageBlockRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	@Override
	public void render(ItemStorageBlockEntity blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {

	}
}
