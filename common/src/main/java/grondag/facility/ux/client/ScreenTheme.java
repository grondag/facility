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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ScreenTheme {
	public final int screenBackground = 0xFFCCCCCC;
	public final int buttonColorActive = 0xFFFFFFFF;
	public final int buttonColorInactive = 0xFFA0A0A0;
	public final int buttonColorFocus = 0xFFBAF6FF;
	public final int textColorActive = 0xFF000000;
	public final int textColorInactive = 0xFFEEEEEE;
	public final int textColorFocus = 0xFF000000;
	public final int textColorLabel = 0xFFFFFFFF;
	public final int textBorder = 0xFF404040;
	public final int textBackground = 0xFFA0A0A0;
	public final int internalMargin = 5;
	public final int externalMargin = 5;
	public final int controlBackground = 0x4AFFFFFF;
	public final int scrollbarWidth = 10;
	public final int itemSlotGradientTop = 0xFFA9A9A9;
	public final int itemSlotGradientBottom = 0xFF898989;
	public final int itemSize = 16;
	public final int itemSpacing = 2;
	public final int itemSelectionMargin = 2;
	public final int itemCaptionHeight = 8;
	public final int itemCaptionColor = 0xFF000000;
	public final int itemSlotSpacing = itemSize + itemSpacing;
	public final int itemRowHeightWithCaption = itemSize + itemCaptionHeight + itemSpacing;
	public final int capacityBarWidth = 4;
	public final int capacityFillColor = 0xFF6080FF;
	public final int capacityEmptyColor = 0xFF404040;
	public final int tabWidth = 8;
	public final int tabMargin = 2;
	public final int singleLineWidgetHeight = 10;

	private static final ObjectArrayList<ScreenTheme> STACK = new ObjectArrayList<>();

	public static ScreenTheme current() {
		return STACK.top();
	}

	static {
		STACK.push(new ScreenTheme());
	}

	public static void push(ScreenTheme theme) {
		STACK.push(theme);
	}

	public static void pop() {
		if (STACK.size() > 1) {
			STACK.pop();
		}
	}
}
