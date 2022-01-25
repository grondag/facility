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

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.menu.MenuRegistry.ScreenFactory;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import io.vram.frex.api.world.BlockEntityRenderData;
import io.vram.modkeys.api.client.ModKeyBinding;

import grondag.facility.Facility;
import grondag.facility.init.BinBlocks;
import grondag.facility.init.CrateBlocks;
import grondag.facility.init.ScreenHandlers;
import grondag.facility.init.TankBlocks;
import grondag.facility.storage.FacilityStorageScreenHandler;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;

public abstract class FacilityClient {
	private FacilityClient() { }

	public static void initialize() {
		registerBeType(CrateBlocks.SLOTTED_CRATE_BLOCK_ENTITY_TYPE(), d -> new StorageBlockRenderer<>(d));
		registerBeType(CrateBlocks.CRATE_BLOCK_ENTITY_TYPE(), d -> new StorageBlockRenderer<>(d));
		registerBeType(BinBlocks.BIN_X1_BLOCK_ENTITY_TYPE(), d -> new BinBlockRenderer(d, 1));
		registerBeType(BinBlocks.BIN_X2_BLOCK_ENTITY_TYPE(), d -> new BinBlockRenderer(d, 2));
		registerBeType(BinBlocks.BIN_X4_BLOCK_ENTITY_TYPE(), d -> new BinBlockRenderer(d, 4));
		registerBeType(BinBlocks.CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE(), d -> new BinBlockRenderer(d, 1));
		registerBeType(BinBlocks.CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE(), d -> new BinBlockRenderer(d, 2));
		registerBeType(BinBlocks.CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE(), d -> new BinBlockRenderer(d, 4));
		registerBeType(TankBlocks.TANK_BLOCK_ENTITY_TYPE(), d -> new TankBlockRenderer(d));

		// Generic inference gets confused without
		final ScreenFactory<FacilityStorageScreenHandler<DiscreteStorageServerDelegate>, ItemStorageScreen> ITEM_SCREEN_FACTORY = (h, i, t) -> new ItemStorageScreen(h, i, t);
		MenuRegistry.registerScreenFactory(ScreenHandlers.CRATE_BLOCK_TYPE(), ITEM_SCREEN_FACTORY);
		MenuRegistry.registerScreenFactory(ScreenHandlers.CRATE_ITEM_TYPE(), ITEM_SCREEN_FACTORY);

		final var forceKey = new KeyMapping("key.facility.force", GLFW.GLFW_KEY_LEFT_CONTROL, "key.facility.category");
		KeyMappingRegistry.register(forceKey);
		ModKeyBinding.setBinding(Facility.FORCE_KEY_NAME, forceKey);

		final var modifyKey = new KeyMapping("key.facility.modify", Minecraft.ON_OSX ? GLFW.GLFW_KEY_LEFT_SUPER : GLFW.GLFW_KEY_LEFT_ALT, "key.facility.category");
		KeyMappingRegistry.register(modifyKey);
		ModKeyBinding.setBinding(Facility.MODIFY_KEY_NAME, modifyKey);

		// Useful when fussing with the color scheme...
		// InvalidateRenderStateCallback.EVENT.register(FacilityColors::init);
	}

	private static <E extends BlockEntity> void registerBeType(BlockEntityType<E> type, BlockEntityRendererProvider<E> blockEntityRendererFactory) {
		BlockEntityRendererRegistry.register(type, blockEntityRendererFactory);
		BlockEntityRenderData.registerProvider(type, be -> be);
	}
}
