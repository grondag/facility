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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.container.Slot;
import net.minecraft.text.TranslatableText;

import grondag.fermion.gui.AbstractSimpleContainerScreen;
import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.control.Button;
import grondag.fermion.gui.control.ItemStackPicker;
import grondag.fermion.gui.control.TextField;
import grondag.fluidity.api.synch.ItemDisplayDelegate;
import grondag.fluidity.api.synch.ItemStorageClientDelegate;
import grondag.fluidity.impl.ItemDisplayDelegateImpl;
import grondag.fonthack.FontHackClient;
import grondag.smart_chest.SmartChestContainer;

public class SmartChestScreen extends AbstractSimpleContainerScreen<SmartChestContainer> implements ContainerProvider<SmartChestContainer> {


	protected int headerHeight;
	protected int storageHeight;

	protected ItemStackPicker<ItemDisplayDelegate> stackPicker;
	protected TextField filterField;
	protected int capacityBarLeft;
	protected int itemPickerTop;
	protected int inventoryLeft;

	public SmartChestScreen(SmartChestContainer container) {
		super(container, MinecraftClient.getInstance().player.inventory, new TranslatableText("Smart Chest"));
	}

	@Override
	public void init() {
		//		font = minecraft.textRenderer;
		font = minecraft.getFontManager().getTextRenderer(FontHackClient.READING_FONT);
		preInitLayout();
		super.init();
	}

	protected void preInitLayout() {

		containerWidth = theme.externalMargin + theme.capacityBarWidth + theme.internalMargin + ItemStackPicker.idealWidth(theme, 9) + theme.externalMargin ;

		headerHeight = theme.singleLineWidgetHeight + theme.externalMargin + theme.internalMargin;
		final int fixedHeight = headerHeight + theme.itemSlotSpacing * 4 + theme.itemSpacing + theme.externalMargin;
		final int availableHeight = MinecraftClient.getInstance().getWindow().getScaledHeight() - 30 - fixedHeight;
		final int storageRows = Math.min(8, availableHeight / theme.itemRowHeightWithCaption);

		storageHeight = storageRows * theme.itemRowHeightWithCaption;
		containerHeight = fixedHeight + storageHeight;

		/** distance from edge of dialog to start of player inventory area */
		inventoryLeft = theme.externalMargin + theme.capacityBarWidth + theme.internalMargin;

		/** distance from top of dialog to start of player inventory area */
		final int playerInventoryTop = containerHeight - theme.externalMargin - theme.itemSlotSpacing * 4 - theme.itemSpacing;

		int i = 0;
		for(int p = 0; p < 3; ++p) {
			for(int o = 0; o < 9; ++o) {
				final Slot oldSlot = container.getSlot(i);
				container.slotList.set(i++, new Slot(oldSlot.inventory, o + p * 9 + 9, inventoryLeft + o * theme.itemSlotSpacing, playerInventoryTop + p * theme.itemSlotSpacing));
			}
		}

		final int rowTop = playerInventoryTop + theme.itemSlotSpacing * 3 + theme.itemSpacing;

		for(int p = 0; p < 9; ++p) {
			final Slot oldSlot = container.getSlot(i);
			container.slotList.set(i++, new Slot(oldSlot.inventory, p, inventoryLeft + p * theme.itemSlotSpacing, rowTop));
		}
	}

	@Override
	protected void computeScreenBounds() {
		super.computeScreenBounds();

		// leave room for REI at bottom if vertical margins are tight
		if(y <= 30) {
			y = 10;
		}
	}

	@Override
	public void addControls() {
		capacityBarLeft = x + theme.externalMargin;
		itemPickerTop = y + headerHeight;
		stackPicker = new ItemStackPicker<>(this, ItemStorageClientDelegate.LIST, null, ItemDisplayDelegate::displayStack, ItemDisplayDelegate::getCount);
		stackPicker.setItemsPerRow(9);

		stackPicker.setLeft(x + inventoryLeft);
		stackPicker.setWidth(ItemStackPicker.idealWidth(theme, 9));
		stackPicker.setTop(itemPickerTop);
		stackPicker.setHeight(storageHeight);
		children.add(stackPicker);

		final Button butt = new Button(this,
				x + containerWidth - 40 - theme.externalMargin, y + theme.externalMargin,
				40, theme.singleLineWidgetHeight,
				ItemDisplayDelegate.getSortLabel(ItemStorageClientDelegate.getSortIndex())) {

			@Override
			public void onClick(double d, double e) {
				final int oldSort = ItemStorageClientDelegate.getSortIndex();
				final int newSort = (oldSort + 1) % ItemDisplayDelegateImpl.SORT_COUNT;
				ItemStorageClientDelegate.setSortIndex(newSort);
				setMessage(ItemDisplayDelegate.getSortLabel(newSort));
				ItemStorageClientDelegate.refreshListIfNeeded();
			}
		};

		this.addButton(butt);

		filterField = new TextField(this,
				x + inventoryLeft, y + theme.externalMargin,
				80, theme.singleLineWidgetHeight, "");
		filterField.setMaxLength(32);
		filterField.setSelected(true);
		filterField.setText(ItemStorageClientDelegate.getFilter());
		filterField.setChangedListener(s -> ItemStorageClientDelegate.setFilter(s));

		children.add(filterField);

		setInitialFocus(filterField);
	}

	@Override
	protected void drawControls(int mouseX, int mouseY, float partialTicks) {
		//PERF: do less frequently
		ItemStorageClientDelegate.refreshListIfNeeded();
		stackPicker.drawControl(mouseX, mouseY, partialTicks);
		filterField.render(mouseX, mouseY, partialTicks);

		final int barHeight = containerHeight - theme.externalMargin * 2;
		final int fillHeight = ItemStorageClientDelegate.capacity() == 0 ? 0 : (int) (barHeight * ItemStorageClientDelegate.usedCapacity() / ItemStorageClientDelegate.capacity());

		// capacity bar
		final int barBottom = y + theme.externalMargin + barHeight;
		GuiUtil.drawRect(capacityBarLeft, y + theme.externalMargin,
				capacityBarLeft + theme.capacityBarWidth, barBottom, theme.capacityEmptyColor);
		GuiUtil.drawRect(capacityBarLeft, barBottom - fillHeight,
				capacityBarLeft + theme.capacityBarWidth, barBottom, theme.capacityFillColor);

		// Draw here because drawforeground currently happens after this
		if(ItemStorageClientDelegate.capacity() > 0 && mouseX >= x + theme.externalMargin && mouseX <= x + theme.externalMargin + theme.capacityBarWidth
				&& mouseY >= y + theme.externalMargin && mouseY <= y + containerHeight - theme.externalMargin) {

			//UGLY: standardize how tooltip coordinates work - wants screen relative but getting window relative as inputs here
			this.drawToolTip(ItemStorageClientDelegate.usedCapacity() + " / " + ItemStorageClientDelegate.capacity(), mouseX - x, mouseY - y);
		}
	}

	// Specific to containers - defined by vanilla
	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		super.drawBackground(partialTicks, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);
	}
}
