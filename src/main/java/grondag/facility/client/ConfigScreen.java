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

import java.util.Arrays;
import java.util.stream.Collectors;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import grondag.facility.FacilityConfig;

@Environment(EnvType.CLIENT)
public class ConfigScreen {
	private static ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();

	static Component[] parse(String key) {
		return Arrays.stream(I18n.get("config.xblocks.help.force_key").split(";")).map(s ->  new TextComponent(s)).collect(Collectors.toList()).toArray(new Component[0]);
	}

	static Screen getScreen(Screen parent) {

		final ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableComponent("config.facility.title")).setSavingRunnable(ConfigScreen::saveUserInput);

		// MISC
		final ConfigCategory misc = builder.getOrCreateCategory(new TranslatableComponent("config.facility.category.misc"));

		misc.addEntry(ENTRY_BUILDER
				.startBooleanToggle(new TranslatableComponent("config.facility.value.shift_screens_left_if_rei_present"), shiftScreensLeftIfReiPresent)
				.setDefaultValue(DEFAULTS.shiftScreensLeftIfReiPresent)
				.setTooltip(parse("config.facility.help.shift_screens_left_if_rei_present"))
				.setSaveConsumer(b -> shiftScreensLeftIfReiPresent = b)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startBooleanToggle(new TranslatableComponent("config.facility.value.use_vanilla_fonts"), useVanillaFonts)
				.setDefaultValue(DEFAULTS.useVanillaFonts)
				.setTooltip(parse("config.facility.help.use_vanilla_fonts"))
				.setSaveConsumer(b -> useVanillaFonts = b)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startIntSlider(new TranslatableComponent("config.facility.value.utb_cat1_rate"), utb1ItemsPerTick, 1, 1024)
				.setDefaultValue(DEFAULTS.utb1ItemsPerTick)
				.setTooltip(parse("config.facility.help.utb_cat1_rate"))
				.setSaveConsumer(i -> utb1ItemsPerTick = i)
				.build());

		misc.addEntry(ENTRY_BUILDER
				.startIntSlider(new TranslatableComponent("config.facility.value.utb_cat1_import_cooldown"), utb1ImporterCooldownTicks, 1, 20)
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
