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

import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.ux.client.GuiUtil;
import grondag.facility.ux.client.ScreenRenderContext;

@Environment(EnvType.CLIENT)
public class VisiblitySelector extends AbstractControl<VisiblitySelector> {
	private final VisibilityPanel target;

	private float buttonHeight;

	public VisiblitySelector(ScreenRenderContext renderContext, VisibilityPanel target) {
		super(renderContext);
		this.target = target;
	}

	@Override
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		float y = top;

		final int hoverIndex = getButtonIndex(mouseX, mouseY);

		for (int i = 0; i < target.size(); i++) {
			final Component label = target.getLabel(i);

			GuiUtil.drawBoxRightBottom(matrixStack.last().pose(), left, y, right, y + buttonHeight, 1, theme.buttonColorActive);
			final int buttonColor = i == hoverIndex ? theme.buttonColorFocus : i == target.getVisiblityIndex() ? theme.buttonColorActive : theme.buttonColorInactive;
			GuiUtil.drawRect(matrixStack.last().pose(), left + 2, y + 2, right - 2, y + buttonHeight - 2, buttonColor);

			final int textColor = i == hoverIndex ? theme.textColorFocus : i == target.getVisiblityIndex() ? theme.textColorActive : theme.textColorInactive;
			GuiUtil.drawAlignedStringNoShadow(matrixStack, renderContext.fontRenderer(), label, left, y, width, buttonHeight,
					textColor, CENTER, MIDDLE);

			y += buttonHeight;
		}
	}

	private int getButtonIndex(double mouseX, double mouseY) {
		refreshContentCoordinatesIfNeeded();

		if (mouseX < left || mouseX > right || buttonHeight == 0) {
			return NO_SELECTION;
		}

		final int selection = (int) ((mouseY - top) / buttonHeight);

		return (selection < 0 || selection >= target.size()) ? NO_SELECTION : selection;
	}

	@Override
	protected void handleCoordinateUpdate() {
		if (target.size() > 0) {
			buttonHeight = height / target.size();
		}
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		final int clickIndex = getButtonIndex(mouseX, mouseY);

		if (clickIndex != NO_SELECTION && clickIndex != target.getVisiblityIndex()) {
			target.setVisiblityIndex(clickIndex);
			GuiUtil.playPressedSound();
		}
	}

	@Override
	public void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
	}
}
