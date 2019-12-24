package grondag.contained.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation.Type;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.contained.block.BinBlockEntity;
import grondag.contained.block.ItemStorageBlockEntity;
import grondag.contained.block.ItemStorageClientState;
import grondag.xm.api.block.XmProperties;

@Environment(EnvType.CLIENT)
public class BinBlockRenderer extends StorageBlockRenderer<BinBlockEntity> {
	protected final int divisionLevel;

	public BinBlockRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, int divisionLevel) {
		super(blockEntityRenderDispatcher);
		this.divisionLevel = divisionLevel;
	}

	@Override
	public void render(BinBlockEntity bin, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightmap, int overlay) {
		super.render(bin, tickDelta, matrixStack, vertexConsumerProvider, lightmap, overlay);
		renderInner(bin, tickDelta, matrixStack, ItemRendererHook.wrap(vertexConsumerProvider), lightmap, overlay);
	}

	protected void renderInner(ItemStorageBlockEntity bin, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightmap, int overlay) {
		final ItemStorageClientState renderState = bin.clientState();

		final float a = renderState.displayAlpha();

		if(a == 0) {
			return;
		}

		final ItemStack[] stacks = renderState.renderStacks;

		if(stacks == null) {
			return;
		}

		// PERF - save this in render state
		final Direction face = bin.getCachedState().get(XmProperties.FACE);
		final World world = bin.getWorld();
		final BlockPos occludingPos = bin.getPos().offset(face);
		final BlockState occludingState = world.getBlockState(occludingPos);

		if(occludingState.isFullOpaque(world, occludingPos)) {
			return;
		}

		matrixStack.push();
		matrixStack.translate(0.5, 0.5, 0.5);
		matrixStack.multiply(face.getOpposite().getRotationQuaternion());
		matrixStack.multiply(X_90);
		matrixStack.translate(0, 0, 0.51);
		ItemRendererHook.enable(a);

		if (divisionLevel == 1) {
			renderFront1(stacks, 1 - a, face, matrixStack, vertexConsumerProvider, WorldRenderer.getLightmapCoordinates(world, occludingPos), overlay);
		} else if (divisionLevel ==2) {
			renderFront2(stacks, 1 - a, face, matrixStack, vertexConsumerProvider, WorldRenderer.getLightmapCoordinates(world, occludingPos), overlay);
		} else {
			renderFront4(stacks, 1 - a, face, matrixStack, vertexConsumerProvider, WorldRenderer.getLightmapCoordinates(world, occludingPos), overlay);
		}

		ItemRendererHook.disable();
		matrixStack.pop();
	}

	private static final Quaternion X_90 = new Quaternion(-90f, 0f, 0f, true);

	protected void renderFront1(ItemStack[] stacks, float d, Direction face, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightMap, int overlay) {
		final ItemStack stack = stacks[0];

		if(stack != null && !stack.isEmpty()) {
			// don't apply scaling to normals

			//TODO: rotate normal up for generated items - improve the lighting - or maybe disable diffuse
			matrixStack.peek().getModel().multiply(Matrix4f.method_24019(0.65f, 0.65f, 0.001f + d * 0.2f));
			ir.renderItem(stacks[0], Type.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider);
		}
	}

	protected void renderFront2(ItemStack[] stacks, float d, Direction face, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightMap, int overlay) {
		matrixStack.peek().getModel().multiply(Matrix4f.method_24019(0.36f, 0.36f, 0.001f + d * 0.2f));

		matrixStack.translate(0, 0.23 / 0.36, 0);
		ItemStack stack = stacks[1];

		if(stack != null && !stack.isEmpty()) {
			ir.renderItem(stack, Type.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider);
		}

		matrixStack.translate(0, -0.46 / 0.36, 0);
		stack = stacks[0];

		if(stack != null && !stack.isEmpty()) {
			ir.renderItem(stack, Type.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider);
		}
	}

	protected void renderFront4(ItemStack[] stacks, float d, Direction face, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightMap, int overlay) {
		matrixStack.peek().getModel().multiply(Matrix4f.method_24019(0.32f, 0.32f, 0.001f + d * 0.2f));

		matrixStack.translate(-0.23 / 0.32, 0.23 / 0.32, 0);
		ItemStack stack = stacks[3];

		if(stack != null && !stack.isEmpty()) {
			ir.renderItem(stack, Type.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider);
		}

		matrixStack.translate(0.46 / 0.32, 0, 0);
		stack = stacks[2];

		if(stack != null && !stack.isEmpty()) {
			ir.renderItem(stack, Type.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider);
		}

		matrixStack.translate(-0.46 / 0.32, -0.46 / 0.32, 0);
		stack = stacks[1];

		if(stack != null && !stack.isEmpty()) {
			ir.renderItem(stack, Type.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider);
		}

		matrixStack.translate(0.46 / 0.32, 0, 0);
		stack = stacks[0];

		if(stack != null && !stack.isEmpty()) {
			ir.renderItem(stack, Type.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider);
		}
	}
}
