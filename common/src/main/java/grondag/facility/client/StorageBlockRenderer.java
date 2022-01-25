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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.storage.StorageBlockEntity;

@Environment(EnvType.CLIENT)
public class StorageBlockRenderer<T extends StorageBlockEntity<?, ?>> implements BlockEntityRenderer<T> {
	protected final Minecraft mc = Minecraft.getInstance();
	protected final ItemRenderer ir = mc.getItemRenderer();

	public StorageBlockRenderer(BlockEntityRendererProvider.Context ctx) {
		// NOOP
	}

	@Override
	public void render(T blockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j) {
		// NOOP
	}
}
