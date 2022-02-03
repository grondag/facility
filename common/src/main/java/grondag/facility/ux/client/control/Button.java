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

import static grondag.facility.ux.client.HorizontalAlignment.CENTER;
import static grondag.facility.ux.client.VerticalAlignment.MIDDLE;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.ux.client.GuiUtil;
import grondag.facility.ux.client.ScreenRenderContext;
import grondag.facility.ux.client.ScreenTheme;

@Environment(EnvType.CLIENT)
public abstract class Button extends AbstractButton {
	protected final ScreenRenderContext renderContext;
	protected final ScreenTheme theme = ScreenTheme.current();

	public Button(ScreenRenderContext renderContext, int x, int y, int width, int height, Component buttonText) {
		super(x, y, width, height, buttonText);
		this.renderContext = renderContext;
	}

	// TODO: add narration logic
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			final int i = getYImage(isHovered);
			final int color = i == 0 ? theme.buttonColorInactive : i == 2 ? theme.buttonColorFocus : theme.buttonColorActive;

			GuiUtil.drawRect(matrixStack.last().pose(), x, y, x + width - 1, y + height - 1, color);
			GuiUtil.drawAlignedStringNoShadow(matrixStack, renderContext.fontRenderer(), getMessage(), x, y, width, height, theme.textColorActive, CENTER, MIDDLE);
		}
	}

	@Override
	public void updateNarration(NarrationElementOutput arg) {
		// TODO whatever this is
	}
}
