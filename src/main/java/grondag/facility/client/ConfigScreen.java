/*******************************************************************************
 * Copyright 2019 grondag
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

import static grondag.facility.FacilityConfig.DEFAULTS;
import static grondag.facility.FacilityConfig.shiftScreensLeftIfReiPresent;
import static grondag.facility.FacilityConfig.useVanillaFonts;
import static grondag.facility.FacilityConfig.utb1ImporterCooldownTicks;
import static grondag.facility.FacilityConfig.utb1ItemsPerTick;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.facility.FacilityConfig;

@Environment(EnvType.CLIENT)
public class ConfigScreen {
	private static ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();

	static Screen getScreen(Screen parent) {

		final ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle("config.facility.title").setSavingRunnable(ConfigScreen::saveUserInput);

		// MISC
		final ConfigCategory misc = builder.getOrCreateCategory("config.facility.category.misc");

		misc.addEntry(ENTRY_BUILDER
				.startBooleanToggle("config.facility.value.shift_screens_left_if_rei_present", shiftScreensLeftIfReiPresent)
				.setDefaultValue(DEFAULTS.shiftScreensLeftIfReiPresent)
				.setTooltip(I18n.translate("config.facility.help.shift_screens_left_if_rei_present").split(";"))
				.setSaveConsumer(b -> shiftScreensLeftIfReiPresent = b)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startBooleanToggle("config.facility.value.use_vanilla_fonts", useVanillaFonts)
				.setDefaultValue(DEFAULTS.useVanillaFonts)
				.setTooltip(I18n.translate("config.facility.help.use_vanilla_fonts").split(";"))
				.setSaveConsumer(b -> useVanillaFonts = b)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startIntSlider("config.facility.value.utb_cat1_rate", utb1ItemsPerTick, 1, 1024)
				.setDefaultValue(DEFAULTS.utb1ItemsPerTick)
				.setTooltip(I18n.translate("config.facility.help.utb_cat1_rate").split(";"))
				.setSaveConsumer(i -> utb1ItemsPerTick = i)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startIntSlider("config.facility.value.utb_cat1_import_cooldown", utb1ImporterCooldownTicks, 1, 20)
				.setDefaultValue(DEFAULTS.utb1ImporterCooldownTicks)
				.setTooltip(I18n.translate("config.facility.help.utb_cat1_import_cooldown").split(";"))
				.setSaveConsumer(i -> utb1ImporterCooldownTicks = i)
				.build());

		return builder.build();
	}

	private static void saveUserInput() {
		FacilityConfig.saveConfig();
	}
}
