/*******************************************************************************
 * Copyright (C) 2019 grondag
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package grondag.smart_chest.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import grondag.fermion.gui.AbstractSimpleContainerScreen;
import grondag.fermion.gui.ContainerLayout;
import grondag.fermion.gui.control.AbstractControl;
import grondag.fermion.gui.control.Button;
import grondag.fermion.gui.control.ItemStackPicker;
import grondag.fermion.gui.control.Panel;
import grondag.fermion.gui.control.TabBar;
import grondag.fonthack.FontHackClient;
import grondag.smart_chest.delegate.ItemDelegate;

public class SmartChestScreen extends AbstractSimpleContainerScreen<PlayerContainer> {

	public static final int BUTTON_ID_SORT = 123;
	public static final int CAPACITY_BAR_WIDTH = 4;

	static ContainerLayout createLayout() {
		final ContainerLayout result = new ContainerLayout();

		result.slotSpacing = 18;

		result.externalMargin = 6;

		result.expectedTextHeight = 12;

		result.dialogWidth = result.externalMargin * 2 + result.slotSpacing * 9 + TabBar.DEFAULT_TAB_WIDTH + CAPACITY_BAR_WIDTH + AbstractControl.CONTROL_INTERNAL_MARGIN;

		result.dialogHeight = result.externalMargin * 3 + result.slotSpacing * 10 + result.expectedTextHeight;

		/** distance from edge of dialog to start of player inventory area */
		result.playerInventoryLeft = result.externalMargin + CAPACITY_BAR_WIDTH + AbstractControl.CONTROL_INTERNAL_MARGIN;

		/** distance from top of dialog to start of player inventory area */
		result.playerInventoryTop = result.dialogHeight - result.externalMargin - result.slotSpacing * 4;

		return result;
	}

	protected ItemStackPicker<ItemDelegate> stackPicker;
	protected Panel stackPanel;
	protected int capacityBarLeft;
	protected int itemPickerTop;
	protected int itemPickerHeight;

	public SmartChestScreen(PlayerContainer container, PlayerInventory playerInventory, Text title) {
		super(createLayout(), container, playerInventory, title);
	}

	@Override
	public void init() {
		font = minecraft.getFontManager().getTextRenderer(FontHackClient.READING_FONT);
		super.init();
	}

	@Override
	public void addControls() {
		final List<ItemDelegate> items = new ArrayList<>();
		final Random rand = ThreadLocalRandom.current();
		final int[] dummy  = new int[1];

		Registry.ITEM.forEach(i -> {
			final ItemStack stack = i.getStackForRender();
			if(!stack.isEmpty() && dummy[0] < 2000) {
				items.add(new ItemDelegate(stack.copy(), rand.nextInt(2000), dummy[0]++));
			}
		});

		//		items.sort((a, b) -> Integer.compare(a.handle(), b.handle()));

		//		for(int i = 0; i < items.size(); i++) {
		//			items.get(i).handle = i;
		//		}

		capacityBarLeft = screenLeft + layout.externalMargin;
		itemPickerTop = screenTop + layout.externalMargin + layout.expectedTextHeight;
		itemPickerHeight = layout.slotSpacing * 6;

		stackPicker = new ItemStackPicker<>(this, items, null);
		stackPicker.setItemsPerRow(9);

		// TODO: remove
		//		stackPanel = new Panel(this, true);
		//		stackPanel.setVerticalLayout(Layout.FIXED);
		//		stackPanel.setHorizontalLayout(Layout.FIXED);
		//		stackPanel.setBackgroundColor(0xFF777777);
		//		stackPanel.setOuterMarginWidth((LAYOUT.slotSpacing - 16) / 2);
		stackPicker.setLeft(screenLeft + layout.playerInventoryLeft);
		stackPicker.setWidth(layout.slotSpacing * 9 + stackPicker.getTabWidth());
		stackPicker.setTop(itemPickerTop);
		stackPicker.setHeight(itemPickerHeight);
		children.add(stackPicker);

		final Button butt = new Button(BUTTON_ID_SORT,
				screenLeft + screenWidth - 40 - layout.externalMargin, screenTop + layout.externalMargin - 2,
				40, fontRenderer().fontHeight + 2,
				"<Sort Label>");
		butt.textColor = 0xFF444444;
		this.addButton(butt);
	}

	@Override
	public void renderBackground() {
		// TODO Auto-generated method stub
		super.renderBackground();
	}

	@Override
	protected void drawControls(int mouseX, int mouseY, float partialTicks) {
		stackPicker.drawControl(mouseX, mouseY, partialTicks);
	}

	// Specific to containers - defined by vanilla
	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {

	}
}
