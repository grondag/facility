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

package grondag.facility.client;

import static grondag.facility.FacilityConfig.DEFAULTS;
import static grondag.facility.FacilityConfig.shiftScreensLeftIfReiPresent;
import static grondag.facility.FacilityConfig.useVanillaFonts;
import static grondag.facility.FacilityConfig.utb1ImporterCooldownTicks;
import static grondag.facility.FacilityConfig.utb1ItemsPerTick;

import java.util.Arrays;
import java.util.stream.Collectors;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.FacilityConfig;

@Environment(EnvType.CLIENT)
public class ConfigScreen {
	private static ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();

	static Component[] parse(String key) {
		return Arrays.stream(I18n.get("config.xblocks.help.force_key").split(";")).map(s -> Component.literal(s)).collect(Collectors.toList()).toArray(new Component[0]);
	}

	static Screen getScreen(Screen parent) {
		final ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable("config.facility.title")).setSavingRunnable(ConfigScreen::saveUserInput);

		// MISC
		final ConfigCategory misc = builder.getOrCreateCategory(Component.translatable("config.facility.category.misc"));

		misc.addEntry(ENTRY_BUILDER
				.startBooleanToggle(Component.translatable("config.facility.value.shift_screens_left_if_rei_present"), shiftScreensLeftIfReiPresent)
				.setDefaultValue(DEFAULTS.shiftScreensLeftIfReiPresent)
				.setTooltip(parse("config.facility.help.shift_screens_left_if_rei_present"))
				.setSaveConsumer(b -> shiftScreensLeftIfReiPresent = b)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startBooleanToggle(Component.translatable("config.facility.value.use_vanilla_fonts"), useVanillaFonts)
				.setDefaultValue(DEFAULTS.useVanillaFonts)
				.setTooltip(parse("config.facility.help.use_vanilla_fonts"))
				.setSaveConsumer(b -> useVanillaFonts = b)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startIntSlider(Component.translatable("config.facility.value.utb_cat1_rate"), utb1ItemsPerTick, 1, 1024)
				.setDefaultValue(DEFAULTS.utb1ItemsPerTick)
				.setTooltip(parse("config.facility.help.utb_cat1_rate"))
				.setSaveConsumer(i -> utb1ItemsPerTick = i)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startIntSlider(Component.translatable("config.facility.value.utb_cat1_import_cooldown"), utb1ImporterCooldownTicks, 1, 20)
				.setDefaultValue(DEFAULTS.utb1ImporterCooldownTicks)
				.setTooltip(parse("config.facility.help.utb_cat1_import_cooldown"))
				.setSaveConsumer(i -> utb1ImporterCooldownTicks = i)
				.build());

		return builder.build();
	}

	private static void saveUserInput() {
		FacilityConfig.saveConfig();
	}
}
