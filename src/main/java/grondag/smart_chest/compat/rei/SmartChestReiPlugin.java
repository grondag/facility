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
package grondag.smart_chest.compat.rei;

import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.DisplayHelper.DisplayBoundsHandler;
import me.shedaniel.rei.api.plugins.REIPluginV0;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;

import grondag.smart_chest.SmartChest;
import grondag.smart_chest.screen.SmartChestScreen;

public class SmartChestReiPlugin implements REIPluginV0 {
	public static final Identifier ID = SmartChest.REG.id("rei_plugin");

	@Override
	public Identifier getPluginIdentifier() {
		return ID;
	}

	@Override
	public SemanticVersion getMinimumVersion() throws VersionParsingException {
		return SemanticVersion.parse("3.0");
	}

	@Override
	public void registerBounds(DisplayHelper displayHelper) {
		final DisplayBoundsHandler<SmartChestScreen> handler = new DisplayBoundsHandler<SmartChestScreen>() {

			@Override
			public Class<?> getBaseSupportedClass() {
				return SmartChestScreen.class;
			}

			//TODO: make these right or remove them
			@Override
			public Rectangle getLeftBounds(SmartChestScreen screen) {
				//return new Rectangle(screen.screenLeft(), screen.screenTop(), screen.width, screen.height);
				return new Rectangle(0, 0, 100, 400);
			}

			@Override
			public Rectangle getRightBounds(SmartChestScreen screen) {
				final Window window = MinecraftClient.getInstance().getWindow();
				final int left = screen.screenLeft() + screen.screenWidth();
				return new Rectangle(left, 0, window.getScaledWidth() - left + 10, window.getScaledHeight());
				//return new Rectangle(screen.screenLeft(), screen.screenTop(), screen.width, screen.height);
			}
		};

		displayHelper.registerBoundsHandler(handler);
	}

}
