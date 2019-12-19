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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

import grondag.fermion.gui.AbstractSimpleContainerScreen;
import grondag.fermion.gui.ContainerLayout;
import grondag.fermion.gui.control.AbstractControl;
import grondag.fermion.gui.control.Button;
import grondag.fermion.gui.control.ItemStackPicker;
import grondag.fermion.gui.control.TabBar;
import grondag.fonthack.FontHackClient;
import grondag.smart_chest.SmartChestContainer;
import grondag.smart_chest.delegate.ItemDelegate;

public class SmartChestScreen extends AbstractSimpleContainerScreen<SmartChestContainer> implements ContainerProvider<SmartChestContainer> {

	public static final int BUTTON_ID_SORT = 123;
	public static final int CAPACITY_BAR_WIDTH = 4;

	private static final int PLAYER_SLOT_SPACING = ItemStackPicker.ITEM_SIZE + ItemStackPicker.ITEM_SPACING;
	private static final int EXTERNAL_MARGIN = 6;

	private static class Layout extends ContainerLayout {
		protected int headerHeight;
		protected int storageHeight;
	}

	static Layout createLayout(SmartChestContainer container) {
		final Layout result = new Layout();

		result.slotSpacing = PLAYER_SLOT_SPACING;

		result.externalMargin = EXTERNAL_MARGIN;

		result.expectedTextHeight = MinecraftClient.getInstance().textRenderer.fontHeight;

		result.dialogWidth = result.externalMargin * 2 + result.slotSpacing * 9 + TabBar.DEFAULT_TAB_WIDTH + CAPACITY_BAR_WIDTH + AbstractControl.CONTROL_INTERNAL_MARGIN;

		result.headerHeight = result.expectedTextHeight + result.externalMargin;
		final int fixedHeight = result.headerHeight + PLAYER_SLOT_SPACING * 4 + AbstractControl.CONTROL_INTERNAL_MARGIN * 2;
		final int availableHeight = MinecraftClient.getInstance().getWindow().getScaledHeight() - 40 - fixedHeight;
		final int storageRows = Math.min(8, availableHeight / ItemStackPicker.ITEM_ROW_HEIGHT);

		result.storageHeight = storageRows * ItemStackPicker.ITEM_ROW_HEIGHT;
		result.dialogHeight = fixedHeight + result.storageHeight;

		/** distance from edge of dialog to start of player inventory area */
		result.playerInventoryLeft = result.externalMargin + CAPACITY_BAR_WIDTH + AbstractControl.CONTROL_INTERNAL_MARGIN;

		/** distance from top of dialog to start of player inventory area */
		result.playerInventoryTop = result.dialogHeight - result.externalMargin - result.slotSpacing * 4;

		int i = 0;
		for(int p = 0; p < 3; ++p) {
			for(int o = 0; o < 9; ++o) {
				final Slot oldSlot = container.getSlot(i);
				container.slotList.set(i++, new Slot(oldSlot.inventory, o + p * 9 + 9, result.playerInventoryLeft + o * PLAYER_SLOT_SPACING, result.playerInventoryTop + p * PLAYER_SLOT_SPACING));
			}
		}

		for(int p = 0; p < 9; ++p) {
			final Slot oldSlot = container.getSlot(i);
			container.slotList.set(i++, new Slot(oldSlot.inventory, p, result.playerInventoryLeft + p * PLAYER_SLOT_SPACING, result.playerInventoryTop + PLAYER_SLOT_SPACING * 3 + 4));
		}
		return result;
	}


	protected ItemStackPicker<ItemDelegate> stackPicker;
	protected int capacityBarLeft;
	protected int itemPickerTop;

	public SmartChestScreen(SmartChestContainer container) {
		super(createLayout(container), container, MinecraftClient.getInstance().player.inventory, new TranslatableText("Smart Chest"));
	}


	@Override
	public void init() {
		//		font = minecraft.textRenderer;
		font = minecraft.getFontManager().getTextRenderer(FontHackClient.READING_FONT);
		super.init();
	}

	@Override
	protected void computeScreenBounds() {
		super.computeScreenBounds();

		// leave room for REI at bottom if vertical margins are tight
		if(y <= 30) {
			y /= 2;
		}
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

		capacityBarLeft = x + ((Layout) layout).externalMargin;
		itemPickerTop = y + ((Layout) layout).headerHeight;
		stackPicker = new ItemStackPicker<>(this, items, null);
		stackPicker.setItemsPerRow(9);

		// TODO: remove
		//		stackPanel = new Panel(this, true);
		//		stackPanel.setVerticalLayout(Layout.FIXED);
		//		stackPanel.setHorizontalLayout(Layout.FIXED);
		//		stackPanel.setBackgroundColor(0xFF777777);
		//		stackPanel.setOuterMarginWidth((LAYOUT.slotSpacing - 16) / 2);
		stackPicker.setLeft(x + layout.playerInventoryLeft);
		stackPicker.setWidth(layout.slotSpacing * 9 + stackPicker.getTabWidth());
		stackPicker.setTop(itemPickerTop);
		stackPicker.setHeight(((Layout) layout).storageHeight);
		children.add(stackPicker);

		final Button butt = new Button(BUTTON_ID_SORT,
				x + screenWidth - 40 - layout.externalMargin, y + layout.externalMargin - 2,
				40, fontRenderer().fontHeight + 2,
				"123");
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
		super.drawBackground(partialTicks, mouseX, mouseY);
	}
}
