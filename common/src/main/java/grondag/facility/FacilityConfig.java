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

package grondag.facility;

import java.io.File;
import java.io.FileOutputStream;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class FacilityConfig {
	@SuppressWarnings("hiding")
	public static class ConfigData {
		@Comment("Gives more space for REI item display.")
		public boolean shiftScreensLeftIfReiPresent = true;

		@Comment("Use Minecraft font in storage screens.")
		public boolean useVanillaFonts = false;

		@Comment("Item transfer rate for category 1 universal transport bus. 1 to 1024")
		public int utb1ItemsPerTick = 4;

		@Comment("Tick frequency for category 1 importer. 1 to 20. Higher values can reduce server impact but may need higher bus transfer rates.")
		public int utb1ImporterCooldownTicks = 5;
	}

	public static final ConfigData DEFAULTS = new ConfigData();
	private static final Gson GSON = new GsonBuilder().create();
	private static final Jankson JANKSON = Jankson.builder().build();
	private static File configFile;

	public static int maxRenderDistance = 32;
	public static long maxRenderDistanceSq = maxRenderDistance * maxRenderDistance;
	public static long keepaliveIntervalMilliseconds = 10000;
	public static boolean shiftScreensLeftIfReiPresent = DEFAULTS.shiftScreensLeftIfReiPresent;
	public static boolean useVanillaFonts = DEFAULTS.useVanillaFonts;
	public static int utb1ItemsPerTick = DEFAULTS.utb1ItemsPerTick;
	public static int utb1ImporterCooldownTicks = DEFAULTS.utb1ImporterCooldownTicks;

	public static void initialize() {
		configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "facility.json5");

		if (configFile.exists()) {
			loadConfig();
		} else {
			saveConfig();
		}
	}

	private static void loadConfig() {
		ConfigData config = new ConfigData();

		try {
			final JsonObject configJson = JANKSON.load(configFile);
			final String regularized = configJson.toJson(false, false, 0);
			config = GSON.fromJson(regularized, ConfigData.class);
		} catch (final Exception e) {
			e.printStackTrace();
			Facility.LOG.error("Unable to load config. Using default values.");
		}

		shiftScreensLeftIfReiPresent = config.shiftScreensLeftIfReiPresent;
		useVanillaFonts = config.useVanillaFonts;
		utb1ItemsPerTick = config.utb1ItemsPerTick;
		utb1ImporterCooldownTicks = config.utb1ImporterCooldownTicks;
	}

	public static void saveConfig() {
		final ConfigData config = new ConfigData();

		config.shiftScreensLeftIfReiPresent = shiftScreensLeftIfReiPresent;
		config.useVanillaFonts = useVanillaFonts;
		config.utb1ItemsPerTick = utb1ItemsPerTick;
		config.utb1ImporterCooldownTicks = utb1ImporterCooldownTicks;

		try {
			final String result = JANKSON.toJson(config).toJson(true, true, 0);

			if (!configFile.exists()) {
				configFile.createNewFile();
			}

			try (FileOutputStream out = new FileOutputStream(configFile, false);) {
				out.write(result.getBytes());
				out.flush();
				out.close();
			}
		} catch (final Exception e) {
			e.printStackTrace();
			Facility.LOG.error("Unable to save config.");
			return;
		}
	}
}
