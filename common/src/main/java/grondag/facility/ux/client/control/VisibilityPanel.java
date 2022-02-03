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
import java.util.Arrays;

import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.ux.client.ScreenRenderContext;

@Environment(EnvType.CLIENT)
public class VisibilityPanel extends Panel {
	private final ArrayList<ArrayList<AbstractControl<?>>> groups = new ArrayList<>();

	private final ArrayList<Component> labels = new ArrayList<>();

	private int visiblityIndex = VisiblitySelector.NO_SELECTION;

	public VisibilityPanel(ScreenRenderContext renderContext, boolean isVertical) {
		super(renderContext, isVertical);
	}

	public int getVisiblityIndex() {
		return visiblityIndex;
	}

	public void setVisiblityIndex(int visiblityIndex) {
		this.visiblityIndex = visiblityIndex;
		children = groups.get(visiblityIndex);
		isDirty = true;
		refreshContentCoordinatesIfNeeded();
	}

	/**
	 * Creates a new visibility group with the given caption and returns its index.
	 * Must call this before adding controls using the index.
	 */
	public int createVisiblityGroup(Component label) {
		labels.add(label);
		groups.add(new ArrayList<AbstractControl<?>>());
		return labels.size() - 1;
	}

	public VisibilityPanel addAll(int visibilityIndex, AbstractControl<?>... controls) {
		groups.get(visibilityIndex).addAll(Arrays.asList(controls));
		isDirty = true;
		return this;
	}

	public VisibilityPanel add(int visibilityIndex, AbstractControl<?> control) {
		groups.get(visibilityIndex).add(control);
		isDirty = true;
		return this;
	}

	public VisibilityPanel remove(int visibilityIndex, int controlindex) {
		groups.get(visibilityIndex).remove(controlindex);
		isDirty = true;
		return this;
	}

	public Component getLabel(int visiblityIndex) {
		return labels.get(visiblityIndex);
	}

	public int size() {
		return labels.size();
	}
}
