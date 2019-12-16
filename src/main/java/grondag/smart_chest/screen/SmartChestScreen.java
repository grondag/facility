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

import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import grondag.fermion.gui.AbstractSimpleScreen;
import grondag.fermion.gui.control.ItemStackPicker;
import grondag.fermion.gui.control.MouseHandler;
import grondag.fermion.gui.control.Panel;
import grondag.fonthack.FontHackClient;
import grondag.smart_chest.delegate.ItemDelegate;

public class SmartChestScreen extends AbstractSimpleScreen {
	public SmartChestScreen() {
		super();
	}

	@Override
	public void init() {
		font = minecraft.getFontManager().getTextRenderer(FontHackClient.READING_FONT);
		super.init();
	}

	@Override
	public void addControls(Panel mainPanel) {
		final List<ItemDelegate> items = new ArrayList<>();
		final Random rand = ThreadLocalRandom.current();

		Registry.ITEM.forEach(i -> {
			final ItemStack stack = i.getStackForRender();
			if(!stack.isEmpty()) {
				items.add(new ItemDelegate(stack.copy(), rand.nextInt(2000), rand.nextInt()));
			}
		});

		items.sort((a, b) -> Integer.compare(a.handle(), b.handle()));

		for(int i = 0; i < items.size(); i++) {
			items.get(i).handle = i;
		}

		mainPanel.add(new ItemStackPicker<>(this, items, font, (MouseHandler<ItemDelegate>)MouseHandler.IGNORE));
	}
}
