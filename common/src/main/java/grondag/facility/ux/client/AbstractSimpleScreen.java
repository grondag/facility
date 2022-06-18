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

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import grondag.facility.ux.client.control.AbstractControl;

public abstract class AbstractSimpleScreen extends Screen implements ScreenRenderContext {
	protected final ScreenTheme theme = ScreenTheme.current();
	protected AbstractControl<?> hoverControl;

	protected int screenLeft;
	protected int screenTop;
	protected int screenWidth;
	protected int screenHeight;

	public AbstractSimpleScreen() {
		super(Component.empty());
	}

	public AbstractSimpleScreen(Component title) {
		super(title);
	}

	@Override
	public void init() {
		super.init();
		computeScreenBounds();
		addControls();
	}

	/**
	 * Called during init before controls are created.
	 */
	protected void computeScreenBounds() {
		screenHeight = height * 4 / 5;
		screenTop = (height - screenHeight) / 2;
		screenWidth = (int) (screenHeight * GuiUtil.GOLDEN_RATIO);
		screenLeft = (width - screenWidth) / 2;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public final void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO: make generic
		// ensure we get updates
		//te.notifyServerPlayerWatching();

		hoverControl = null;

		renderBackground(matrixStack);

		// shouldn't do anything but call in case someone is hooking it
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		drawControls(matrixStack, mouseX, mouseY, partialTicks);

		if (hoverControl != null) {
			hoverControl.drawToolTip(matrixStack, mouseX, mouseY, partialTicks);
		}
	}

	protected void drawControls(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		final List<? extends GuiEventListener> children = children();
		final int limit = children.size();

		for (int i = 0; i < limit; ++i) {
			final GuiEventListener e = children.get(i);

			if (e instanceof AbstractControl) {
				((AbstractControl<?>) children.get(i)).render(matrixStack, mouseX, mouseY, partialTicks);
			}
		}
	}

	@Override
	public void addControls() {
		// NOOP
	}

	@Override
	public Minecraft minecraft() {
		return minecraft;
	}

	@Override
	public ItemRenderer renderItem() {
		return itemRenderer;
	}

	@Override
	public Screen screen() {
		return this;
	}

	@Override
	public Font fontRenderer() {
		return font;
	}

	@Override
	public void setHoverControl(AbstractControl<?> control) {
		hoverControl = control;
	}

	@Override
	public int screenLeft() {
		return screenLeft;
	}

	@Override
	public int screenWidth() {
		return screenWidth;
	}

	@Override
	public int screenTop() {
		return screenTop;
	}

	@Override
	public int screenHeight() {
		return screenHeight;
	}

	@Override
	public Optional<GuiEventListener> getChildAt(double double_1, double double_2) {
		return Optional.ofNullable(hoverControl);
	}

	@Override
	public void renderTooltip(PoseStack matrixStack, ItemStack itemStack, int i, int j) {
		super.renderTooltip(matrixStack, itemStack, i, j);
	}
}
