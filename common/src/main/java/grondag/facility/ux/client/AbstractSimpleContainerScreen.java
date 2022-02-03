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

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import grondag.facility.ux.client.control.AbstractControl;

public abstract class AbstractSimpleContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements ScreenRenderContext {
	protected AbstractControl<?> hoverControl;
	protected final ScreenTheme theme = ScreenTheme.current();

	public AbstractSimpleContainerScreen(T container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
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
		topPos = (height - imageHeight) / 2;
		leftPos = (width - imageWidth) / 2;
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBackground(matrixStack);
		fill(matrixStack, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, theme.screenBackground);

		final int limit = menu.slots.size();

		// player slot backgrounds
		for (int i = 0; i < limit; i++) {
			final Slot slot = menu.getSlot(i);
			final int u = slot.x + leftPos;
			final int v = slot.y + topPos;
			fillGradient(matrixStack, u, v, u + theme.itemSize, v + theme.itemSize, theme.itemSlotGradientTop, theme.itemSlotGradientBottom);
		}
	}

	@Override
	public final void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO: make generic
		// ensure we get updates
		//te.notifyServerPlayerWatching();

		hoverControl = null;

		super.render(matrixStack, mouseX, mouseY, partialTicks);

		for (int k = 0; k < children().size(); ++k) {
			((Widget) children().get(k)).render(matrixStack, mouseX, mouseY, partialTicks);
		}

		drawControls(matrixStack, mouseX, mouseY, partialTicks);

		if (hoverControl != null) {
			hoverControl.drawToolTip(matrixStack, mouseX, mouseY, partialTicks);
		}

		RenderSystem.disableBlend();

		if (hoveredSlot != null) {
			final int sx = leftPos + hoveredSlot.x;
			final int sy = topPos + hoveredSlot.y;
			GuiUtil.drawBoxRightBottom(matrixStack.last().pose(), sx - theme.itemSelectionMargin, sy - theme.itemSelectionMargin, sx + theme.itemSize + theme.itemSelectionMargin,
					sy + theme.itemSize + theme.itemSelectionMargin, 1, theme.buttonColorFocus);
		}

		//	      this.nameField.render(i, j, f);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	protected abstract void drawControls(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);

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
	public void renderTooltip(PoseStack matrixStack, ItemStack hoverStack, int mouseX, int mouseY) {
		super.renderTooltip(matrixStack, hoverStack, mouseX, mouseY);
	}

	@Override
	public int screenLeft() {
		return leftPos;
	}

	@Override
	public int screenWidth() {
		return imageWidth;
	}

	@Override
	public int screenTop() {
		return topPos;
	}

	@Override
	public int screenHeight() {
		return imageHeight;
	}

	@Override
	public Optional<GuiEventListener> getChildAt(double double_1, double double_2) {
		return Optional.ofNullable(hoverControl);
	}

	// like private vanilla method but doesn't test isActive for the slot
	public Slot findHoveredSlot(double x, double y) {
		for (int i = 0; i < menu.slots.size(); ++i) {
			final Slot slot = menu.slots.get(i);

			if (this.isHovering(slot, x, y)) {
				return slot;
			}
		}

		return null;
	}

	public boolean isHovering(Slot slot, double x, double y) {
		return isHovering(slot.x, slot.y, 16, 16, x, y);
	}
}
