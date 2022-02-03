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

package grondag.facility.ux.client.control;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.ux.client.GuiUtil;
import grondag.facility.ux.client.ScreenRenderContext;

@Environment(EnvType.CLIENT)
public class ItemPreview extends AbstractControl<ItemPreview> {
	public ItemPreview(ScreenRenderContext renderContext) {
		super(renderContext);
	}

	public ItemStack previewItem;

	private float contentLeft;
	private float contentTop;
	private float contentSize;

	@Override
	public void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (previewItem != null) {
			GuiUtil.renderItemAndEffectIntoGui(renderContext, previewItem, contentLeft, contentTop, contentSize);
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		contentSize = Math.min(width, height);
		contentLeft = left + (width - contentSize) / 2;
		contentTop = top + (height - contentSize) / 2;
	}

	@Override
	public void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
	}
}
