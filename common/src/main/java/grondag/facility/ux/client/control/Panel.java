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

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.events.ContainerEventHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.ux.client.GuiUtil;
import grondag.facility.ux.client.Layout;
import grondag.facility.ux.client.ScreenRenderContext;

@Environment(EnvType.CLIENT)
public class Panel extends AbstractParentControl<Panel> implements ContainerEventHandler {
	/** If false is horizontal. */
	public final boolean isVertical;

	private int outerMarginWidth = 0;
	private int innerMarginWidth = 0;

	/**
	 * If true, don't adjustIfEnabled layout of any child controls. Useful for
	 * containers that have to conform to a specific pixel layout.
	 */
	private boolean isLayoutDisabled = false;

	public Panel(ScreenRenderContext renderContext, boolean isVertical) {
		super(renderContext);
		this.isVertical = isVertical;
	}

	public Panel addAll(AbstractControl<?>... controls) {
		children.addAll(Arrays.asList(controls));
		isDirty = true;
		return this;
	}

	public Panel add(AbstractControl<?> control) {
		children.add(control);
		isDirty = true;
		return this;
	}

	@Override
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (getBackgroundColor() != 0) {
			GuiUtil.drawRect(matrixStack.last().pose(), left, top, right, bottom, getBackgroundColor());
		}

		for (final AbstractControl<?> control : children) {
			control.render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		if (isLayoutDisabled || children == null || children.isEmpty()) {
			return;
		}

		int totalWeight = 0;
		int totalFixed = 0;

		final float variableSpace = (isVertical ? height : width) - outerMarginWidth * 2;
		final float fixedSpace = (isVertical ? width : height) - outerMarginWidth * 2;

		// on start pass, gather the size/weights for the expanding dimension
		for (final AbstractControl<?> control : children) {
			if (isVertical) {
				switch (control.getVerticalLayout()) {
					case FIXED:
						totalFixed += control.getHeight();
						break;

					case PROPORTIONAL:
						totalFixed += fixedSpace * control.getAspectRatio();
						break;

					case WEIGHTED:
					default:
						totalWeight += control.getVerticalWeight();
						break;
				}
			} else {
				switch (control.getHorizontalLayout()) {
					case FIXED:
						totalFixed += control.getWidth();
						break;

					case PROPORTIONAL:
						totalFixed += fixedSpace / control.getAspectRatio();
						break;

					case WEIGHTED:
					default:
						totalWeight += control.getHorizontalWeight();
						break;
				}
			}
		}

		// now scale the weights to the amount of space available
		final float spaceFactor = totalWeight <= 0 ? 0 : (variableSpace - totalFixed - innerMarginWidth * (children.size() - 1)) / totalWeight;

		float contentLeft = left + outerMarginWidth;
		float contentTop = top + outerMarginWidth;
		final float fixedSize = (isVertical ? width : height) - outerMarginWidth * 2;

		// on second pass rescale
		for (final AbstractControl<?> control : children) {
			//            double variableSize;

			float controlHeight;
			float controlWidth;

			if (isVertical) {
				controlWidth = control.getHorizontalLayout() == Layout.FIXED ? control.getWidth() : fixedSize;

				switch (control.getVerticalLayout()) {
					case FIXED:
						controlHeight = control.getHeight();
						break;

					case PROPORTIONAL:
						controlHeight = controlWidth * control.getAspectRatio();
						break;

					case WEIGHTED:
					default:
						controlHeight = spaceFactor * control.getVerticalWeight();
						break;
				}

				if (control.getHorizontalLayout() == Layout.PROPORTIONAL) {
					controlWidth = controlHeight / control.getAspectRatio();
				}

				control.resize(contentLeft, contentTop, controlWidth, controlHeight);
				contentTop += controlHeight + innerMarginWidth;
			} else {
				controlHeight = control.getVerticalLayout() == Layout.FIXED ? control.getHeight() : fixedSize;

				switch (control.getHorizontalLayout()) {
					case FIXED:
						controlWidth = control.getWidth();
						break;

					case PROPORTIONAL:
						controlWidth = controlHeight / control.getAspectRatio();
						break;

					case WEIGHTED:
					default:
						controlWidth = spaceFactor * control.getHorizontalWeight();
						break;
				}

				if (control.getVerticalLayout() == Layout.PROPORTIONAL) {
					controlHeight = controlWidth * control.getAspectRatio();
				}

				control.resize(contentLeft, contentTop, controlWidth, controlHeight);
				contentLeft += controlWidth + innerMarginWidth;
			}
		}
	}

	//TODO: remove - should not longer be needed because events always go to hovered element

	//	@Override
	//	public boolean handleMouseClick(MinecraftClient mc, double mouseX, double mouseY, int clickedMouseButton) {
	//		for (final AbstractControl<?> child : children) {
	//			child.mouseClick(mc, mouseX, mouseY, clickedMouseButton);
	//		}
	//		return true;
	//	}
	//
	//	@Override
	//	public void handleMouseDrag(MinecraftClient mc, int mouseX, int mouseY, int clickedMouseButton) {
	//		for (final AbstractControl<?> child : children) {
	//			child.mouseDrag(mc, mouseX, mouseY, clickedMouseButton);
	//		}
	//	}
	//
	//	@Override
	//	protected void handleMouseScroll(int mouseX, int mouseY, int scrollDelta) {
	//		for (final AbstractControl<?> child : children) {
	//			child.mouseScroll(mouseX, mouseY, scrollDelta);
	//		}
	//	}

	/** The pixelWidth of the background from the edge of child controls. */
	public int getOuterMarginWidth() {
		return outerMarginWidth;
	}

	/** Sets the pixelWidth of the background from the edge of child controls. */
	public Panel setOuterMarginWidth(int outerMarginWidth) {
		this.outerMarginWidth = outerMarginWidth;
		isDirty = true;
		return this;
	}

	/** The spacing between child controls. */
	public int getInnerMarginWidth() {
		return innerMarginWidth;
	}

	/** Sets the spacing between child controls. */
	public Panel setInnerMarginWidth(int innerMarginWidth) {
		this.innerMarginWidth = innerMarginWidth;
		isDirty = true;
		return this;
	}

	/**
	 * Set true to disable automatic layout of child controls. Used for containers
	 * that require a fixed layout. Means you must write code to set position and
	 * size of all children.
	 */
	public boolean isLayoutDisabled() {
		return isLayoutDisabled;
	}

	public void setLayoutDisabled(boolean isLayoutDisabled) {
		this.isLayoutDisabled = isLayoutDisabled;
	}

	@Override
	public void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
	}
}
