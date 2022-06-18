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

package grondag.facility.ux.client;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import grondag.facility.ux.client.control.AbstractControl;

public interface ScreenRenderContext {
	Minecraft minecraft();

	ItemRenderer renderItem();

	Screen screen();

	Font fontRenderer();

	/**
	 * controls that are being hovered over while rendering should call this to
	 * receive a callback after all controls have been rendered to draw a tooltip.
	 */
	void setHoverControl(AbstractControl<?> control);

	default void renderTooltip(PoseStack matrixStack, ItemStack itemStack, int i, int j) {
		screen().renderComponentTooltip(matrixStack, screen().getTooltipFromItem(itemStack), i, j);
	}

	default void drawLocalizedToolTip(PoseStack matrixStack, String lang_key, int mouseX, int mouseY) {
		screen().renderTooltip(matrixStack, Component.translatable(lang_key), mouseX, mouseY);
	}

	default void drawLocalizedToolTip(PoseStack matrixStack, int mouseX, int mouseY, String... lang_keys) {
		if (lang_keys.length == 0) {
			return;
		}

		final ArrayList<Component> list = new ArrayList<>(lang_keys.length);

		for (final String key : lang_keys) {
			list.add(Component.translatable(key));
		}

		screen().renderComponentTooltip(matrixStack, list, mouseX, mouseY);
	}

	default void drawLocalizedToolTipBoolean(PoseStack matrixStack, boolean bool, String true_key, String false_key, int mouseX, int mouseY) {
		screen().renderTooltip(matrixStack, Component.translatable(bool ? true_key : false_key), mouseX, mouseY);
	}

	int screenLeft();

	int screenWidth();

	int screenTop();

	int screenHeight();

	void addControls();
}
