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

package grondag.facility.varia;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import io.vram.dtk.Bits;

import grondag.facility.Facility;

/**
 * Generates 1 to 4 digit alphanumeric IDs from input values. Used for machine
 * names.
 */
public class Base32Namer {
	private static char[] GLYPHS = "0123456789ABCDEFGHJKLMNPRTUVWXYZ".toCharArray();

	private static HashSet<String> LOWER_CASE_BAD_NAMES = new HashSet<>();

	private static long NAME_BIT_MASK = Bits.longBitMask(20);

	public static void loadBadNams(ResourceManager resourceManager, ResourceLocation id) {
		try {
			final var res = resourceManager.getResource(id);
			final Gson g = new Gson();
			final JsonObject json = g.fromJson((Reader) new InputStreamReader(res.get().open(), StandardCharsets.UTF_8), JsonObject.class);
			final var badNames = json.getAsJsonArray("offensive_names");
			LOWER_CASE_BAD_NAMES.clear();

			for (final var s : badNames) {
				LOWER_CASE_BAD_NAMES.add(s.getAsString().toLowerCase(Locale.ROOT));
			}
		} catch (final Exception e) {
			Facility.LOG.warn("Unable to parse bad names.  Naughtiness might ensue.");
		}
	}

	public static void loadBadNames(String... badNames) {
		LOWER_CASE_BAD_NAMES.clear();

		for (final String s : badNames) {
			LOWER_CASE_BAD_NAMES.add(s.toLowerCase(Locale.ROOT));
		}
	}

	public static boolean isBadName(String name) {
		return LOWER_CASE_BAD_NAMES.contains(name.toLowerCase(Locale.ROOT));
	}

	public static String makeRawName(int num) {
		final char[] digits = new char[4];
		digits[0] = GLYPHS[num >> 15 & 31];
		digits[1] = GLYPHS[num >> 10 & 31];
		digits[2] = GLYPHS[num >> 5 & 31];
		digits[3] = GLYPHS[num & 31];

		if (num < 0) {
			num = -num;
		}

		if (num > 32767) {
			// common 4-digit name
			return new String(digits, 0, 4);
		} else if (num > 1023) {
			// uncommon 3-digit name
			return new String(digits, 1, 3);
		} else if (num > 31) {
			// rare 2-digit name
			return new String(digits, 2, 2);
		} else {
			// ultra rare one-digit name
			return String.valueOf(digits[3]);
		}
	}

	public static String makeFilteredName(long num) {
		for (int i = 0; i < 3; i++) {
			final int n = (int) ((num >> (20 * i)) & NAME_BIT_MASK);

			if (n != 0) {
				final String s = makeRawName(n);

				if (!isBadName(s)) {
					return s;
				}
			}
		}

		return "N1CE";
	}

	public static String makeName(long num, boolean filter) {
		return filter ? makeFilteredName(num) : makeRawName((int) num);
	}
}
