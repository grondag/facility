/*******************************************************************************
 * Copyright 2019, 2020 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.facility.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import net.fabricmc.loader.api.FabricLoader;

import grondag.facility.FacilityConfig;
import grondag.facility.storage.item.CrateContainer;
import grondag.fermion.gui.AbstractSimpleContainerScreen;
import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.control.Button;
import grondag.fermion.gui.control.ItemStackPicker;
import grondag.fermion.gui.control.TextField;
import grondag.fluidity.base.synch.DiscreteDisplayDelegate;
import grondag.fluidity.base.synch.DiscreteStorageClientDelegate;
import grondag.fluidity.base.synch.DisplayDelegate;
import grondag.fluidity.base.synch.ItemStorageAction;
import grondag.fluidity.impl.DiscreteDisplayDelegateImpl;
import grondag.fonthack.FontHackClient;

public class ItemStorageScreen extends AbstractSimpleContainerScreen<CrateContainer> implements ScreenHandlerProvider<CrateContainer> {
	private static DiscreteStorageClientDelegate DELEGATE = DiscreteStorageClientDelegate.INSTANCE;

	protected int headerHeight;
	protected int storageHeight;

	protected ItemStackPicker<DiscreteDisplayDelegate> stackPicker;
	protected TextField filterField;
	protected int capacityBarLeft;
	protected int itemPickerTop;
	protected int inventoryLeft;

	@SuppressWarnings("resource")
	public ItemStorageScreen(CrateContainer container) {
		// TODO: something something localization
		super(container, MinecraftClient.getInstance().player.inventory, new TranslatableText("Facility Storage"));
	}

	@Override
	public void init() {
		DELEGATE.setFilter("");
		textRenderer = FacilityConfig.useVanillaFonts ? client.textRenderer : FontHackClient.getTextRenderer(FontHackClient.READING_FONT);

		preInitLayout();
		super.init();
	}

	protected void preInitLayout() {

		backgroundWidth = theme.externalMargin + theme.capacityBarWidth + theme.internalMargin + ItemStackPicker.idealWidth(theme, 9) + theme.externalMargin ;

		headerHeight = theme.singleLineWidgetHeight + theme.externalMargin + theme.internalMargin;
		final int fixedHeight = headerHeight + theme.itemSlotSpacing * 4 + theme.itemSpacing + theme.externalMargin;
		final int availableHeight = MinecraftClient.getInstance().getWindow().getScaledHeight() - 30 - fixedHeight;
		final int storageRows = Math.min(8, availableHeight / theme.itemRowHeightWithCaption);

		storageHeight = storageRows * theme.itemRowHeightWithCaption;
		backgroundHeight = fixedHeight + storageHeight;

		/** distance from edge of dialog to start of player inventory area */
		inventoryLeft = theme.externalMargin + theme.capacityBarWidth + theme.internalMargin;

		/** distance from top of dialog to start of player inventory area */
		final int playerInventoryTop = backgroundHeight - theme.externalMargin - theme.itemSlotSpacing * 4 - theme.itemSpacing;

		int i = 0;

		for(int p = 0; p < 3; ++p) {
			for(int o = 0; o < 9; ++o) {
				final Slot oldSlot = handler.getSlot(i);
				final Slot newSlot = new Slot(oldSlot.inventory, o + p * 9 + 9, inventoryLeft + o * theme.itemSlotSpacing, playerInventoryTop + p * theme.itemSlotSpacing);
				newSlot.id = oldSlot.id;
				handler.slots.set(i++, newSlot);
			}
		}

		final int rowTop = playerInventoryTop + theme.itemSlotSpacing * 3 + theme.itemSpacing;

		for(int p = 0; p < 9; ++p) {
			final Slot oldSlot = handler.getSlot(i);
			final Slot newSlot = new Slot(oldSlot.inventory, p, inventoryLeft + p * theme.itemSlotSpacing, rowTop);
			newSlot.id = oldSlot.id;
			handler.slots.set(i++, newSlot);
		}
	}

	@Override
	protected void computeScreenBounds() {
		y = (height - backgroundHeight) / 2;

		// if using REI, center on left 2/3 of screen to allow more room for REI
		if(FacilityConfig.shiftScreensLeftIfReiPresent &&  FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
			x = ((width * 2 / 3) - backgroundWidth) / 2;
		} else {
			x = (width - backgroundWidth) / 2;
		}

		// leave room for REI at bottom if vertical margins are tight
		if(y <= 30) {
			y = 10;
		}
	}

	@Override
	public void addControls() {
		capacityBarLeft = x + theme.externalMargin;
		itemPickerTop = y + headerHeight;
		stackPicker = new ItemStackPicker<>(this, DELEGATE.LIST, ItemStorageAction::selectAndSend, d -> d.article().toStack(), DiscreteDisplayDelegate::getCount);
		stackPicker.setItemsPerRow(9);

		stackPicker.setLeft(x + inventoryLeft);
		stackPicker.setWidth(ItemStackPicker.idealWidth(theme, 9));
		stackPicker.setTop(itemPickerTop);
		stackPicker.setHeight(storageHeight);
		children.add(stackPicker);

		final Button butt = new Button(this,
				x + backgroundWidth - 40 - theme.externalMargin, y + theme.externalMargin,
				40, theme.singleLineWidgetHeight,
				DisplayDelegate.getSortText(DELEGATE.getSortIndex())) {

			@Override
			public void onClick(double d, double e) {
				final int oldSort = DELEGATE.getSortIndex();
				final int newSort = (oldSort + 1) % DiscreteDisplayDelegateImpl.SORT_COUNT;
				DELEGATE.setSortIndex(newSort);
				setMessage(DisplayDelegate.getSortText(newSort));
				DELEGATE.refreshListIfNeeded();
			}
		};

		this.addButton(butt);

		filterField = new TextField(this,
				x + inventoryLeft, y + theme.externalMargin,
				80, theme.singleLineWidgetHeight, new LiteralText(""));
		filterField.setMaxLength(32);
		filterField.setSelected(true);
		filterField.setChangedListener(s -> DELEGATE.setFilter(s));

		children.add(filterField);

		setInitialFocus(filterField);
	}

	@Override
	protected void drawControls(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		//PERF: do less frequently
		DELEGATE.refreshListIfNeeded();
		stackPicker.drawControl(matrixStack, mouseX, mouseY, partialTicks);
		filterField.render(matrixStack, mouseX, mouseY, partialTicks);

		final int barHeight = backgroundHeight - theme.externalMargin * 2;
		final int fillHeight = DELEGATE.capacity() == 0 ? 0 : (int) (barHeight * DELEGATE.usedCapacity() / DELEGATE.capacity());

		// capacity bar
		final int barBottom = y + theme.externalMargin + barHeight;
		GuiUtil.drawRect(capacityBarLeft, y + theme.externalMargin,
				capacityBarLeft + theme.capacityBarWidth, barBottom, theme.capacityEmptyColor);
		GuiUtil.drawRect(capacityBarLeft, barBottom - fillHeight,
				capacityBarLeft + theme.capacityBarWidth, barBottom, theme.capacityFillColor);

		// Draw here because drawforeground currently happens after this
		if(DELEGATE.capacity() > 0 && mouseX >= x + theme.externalMargin && mouseX <= x + theme.externalMargin + theme.capacityBarWidth
				&& mouseY >= y + theme.externalMargin && mouseY <= y + backgroundHeight - theme.externalMargin) {

			//UGLY: standardize how tooltip coordinates work - wants screen relative but getting window relative as inputs here
			this.renderTooltip(matrixStack, new LiteralText(DELEGATE.usedCapacity() + " / " + DELEGATE.capacity()), mouseX - x, mouseY - y);
		}
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		// don't draw text
		// NOOP
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		return super.mouseClicked(x, y, mouseButton);
	}

	@Override
	public boolean mouseDragged(double onX, double onY, int mouseButton, double fromX, double fromY) {
		final Slot slot = getSlotAt(onX, onY);

		if (!client.options.touchscreen && !isCursorDragging && slot != null && slot.hasStack() && hasShiftDown() && client.player.inventory.getCursorStack().isEmpty()) {
			onMouseClick(slot, slot.id, mouseButton, SlotActionType.QUICK_MOVE);
			return true;
		}

		return super.mouseDragged(onX, onY, mouseButton, fromX, fromY);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return super.mouseReleased(d, e, i);
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int mouseButton, SlotActionType slotActionType) {
		super.onMouseClick(slot, slotId, mouseButton, slotActionType);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return super.keyPressed(i, j, k);
	}
}
