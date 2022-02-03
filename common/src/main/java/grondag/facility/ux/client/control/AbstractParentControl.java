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

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

import grondag.facility.ux.client.ScreenRenderContext;

public abstract class AbstractParentControl<T extends AbstractParentControl<T>> extends AbstractControl<T> implements ContainerEventHandler {
	@Nullable
	private GuiEventListener focused;
	private boolean isDragging;
	protected ArrayList<AbstractControl<?>> children = new ArrayList<>();

	public AbstractParentControl(ScreenRenderContext renderContext) {
		super(renderContext);
	}

	@Override
	public final boolean isDragging() {
		return this.isDragging;
	}

	@Override
	public final void setDragging(boolean dragging) {
		this.isDragging = dragging;
	}

	@Override
	@Nullable
	public GuiEventListener getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(@Nullable GuiEventListener element) {
		this.focused = element;
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return children;
	}

	@Override
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		ContainerEventHandler.super.mouseClicked(mouseX, mouseY, clickedMouseButton);
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		ContainerEventHandler.super.mouseDragged(mouseX, mouseY, clickedMouseButton, dx, dy);
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}
}
