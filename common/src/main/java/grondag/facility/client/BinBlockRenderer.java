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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.storage.item.BinBlockEntity;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.facility.storage.item.CrateClientState;
import grondag.xm.api.block.XmProperties;

@Environment(EnvType.CLIENT)
public class BinBlockRenderer extends StorageBlockRenderer<BinBlockEntity> {
	protected final int divisionLevel;

	public BinBlockRenderer(BlockEntityRendererProvider.Context ctx, int divisionLevel) {
		super(ctx);
		this.divisionLevel = divisionLevel;
	}

	@Override
	public void render(BinBlockEntity bin, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int lightmap, int overlay) {
		super.render(bin, tickDelta, matrixStack, vertexConsumerProvider, lightmap, overlay);
		renderInner(bin, tickDelta, matrixStack, RendererHooks.wrap(vertexConsumerProvider), lightmap, overlay);
	}

	protected void renderInner(CrateBlockEntity bin, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int lightmap, int overlay) {
		final CrateClientState renderState = bin.clientState();

		final float a = renderState.displayAlpha();

		if (a == 0) {
			return;
		}

		final ItemStack[] stacks = renderState.renderStacks;

		if (stacks == null) {
			return;
		}

		// PERF - save this in render state
		final Direction face = bin.getBlockState().getValue(XmProperties.FACE);
		final Level world = bin.getLevel();
		final BlockPos occludingPos = bin.getBlockPos().relative(face.getOpposite());
		final BlockState occludingState = world.getBlockState(occludingPos);

		if (occludingState.isSolidRender(world, occludingPos)) {
			return;
		}

		matrixStack.pushPose();
		matrixStack.translate(0.5, 0.5, 0.5);
		matrixStack.mulPose(face.getOpposite().getRotation());
		matrixStack.mulPose(X_90);
		matrixStack.translate(0, 0, 0.51);
		RendererHooks.enable(a);

		if (divisionLevel == 1) {
			renderFront1(stacks, 1 - a, face, matrixStack, vertexConsumerProvider, LevelRenderer.getLightColor(world, occludingPos), overlay);
		} else if (divisionLevel == 2) {
			renderFront2(stacks, 1 - a, face, matrixStack, vertexConsumerProvider, LevelRenderer.getLightColor(world, occludingPos), overlay);
		} else {
			renderFront4(stacks, 1 - a, face, matrixStack, vertexConsumerProvider, LevelRenderer.getLightColor(world, occludingPos), overlay);
		}

		RendererHooks.disable();
		matrixStack.popPose();
	}

	private static final Quaternion X_90 = new Quaternion(-90f, 0f, 0f, true);

	protected void renderFront1(ItemStack[] stacks, float d, Direction face, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int lightMap, int overlay) {
		final ItemStack stack = stacks[0];

		if (stack != null && !stack.isEmpty()) {
			// don't apply scaling to normals

			//TODO: rotate normal up for generated items - improve the lighting - or maybe disable diffuse
			matrixStack.last().pose().multiply(Matrix4f.createScaleMatrix(0.65f, 0.65f, 0.001f + d * 0.2f));
			ir.renderStatic(stacks[0], TransformType.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider, 42);
		}
	}

	protected void renderFront2(ItemStack[] stacks, float d, Direction face, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int lightMap, int overlay) {
		matrixStack.last().pose().multiply(Matrix4f.createScaleMatrix(0.36f, 0.36f, 0.001f + d * 0.2f));

		matrixStack.translate(0, 0.23 / 0.36, 0);
		ItemStack stack = stacks[0];

		if (stack != null && !stack.isEmpty()) {
			ir.renderStatic(stack, TransformType.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider, 42);
		}

		matrixStack.translate(0, -0.46 / 0.36, 0);
		stack = stacks[1];

		if (stack != null && !stack.isEmpty()) {
			ir.renderStatic(stack, TransformType.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider, 42);
		}
	}

	protected void renderFront4(ItemStack[] stacks, float d, Direction face, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int lightMap, int overlay) {
		matrixStack.last().pose().multiply(Matrix4f.createScaleMatrix(0.32f, 0.32f, 0.001f + d * 0.2f));

		matrixStack.translate(-0.23 / 0.32, 0.23 / 0.32, 0);
		ItemStack stack = stacks[0];

		if (stack != null && !stack.isEmpty()) {
			ir.renderStatic(stack, TransformType.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider, 42);
		}

		matrixStack.translate(0.46 / 0.32, 0, 0);
		stack = stacks[1];

		if (stack != null && !stack.isEmpty()) {
			ir.renderStatic(stack, TransformType.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider, 42);
		}

		matrixStack.translate(-0.46 / 0.32, -0.46 / 0.32, 0);
		stack = stacks[2];

		if (stack != null && !stack.isEmpty()) {
			ir.renderStatic(stack, TransformType.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider, 42);
		}

		matrixStack.translate(0.46 / 0.32, 0, 0);
		stack = stacks[3];

		if (stack != null && !stack.isEmpty()) {
			ir.renderStatic(stack, TransformType.GUI, lightMap, overlay, matrixStack, vertexConsumerProvider, 42);
		}
	}
}
