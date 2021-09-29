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

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

import io.vram.frex.api.world.BlockEntityRenderData;

import grondag.facility.init.BinBlocks;
import grondag.facility.init.CrateBlocks;
import grondag.facility.init.ScreenHandlers;
import grondag.facility.init.TankBlocks;
import grondag.facility.storage.FactilityStorageScreenHandler;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;

public enum ClientRegistrations {
	;

	static {
		registerBeType(CrateBlocks.SLOTTED_CRATE_BLOCK_ENTITY_TYPE, d -> new StorageBlockRenderer<>(d));
		registerBeType(CrateBlocks.CRATE_BLOCK_ENTITY_TYPE, d -> new StorageBlockRenderer<>(d));
		registerBeType(BinBlocks.BIN_X1_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 1));
		registerBeType(BinBlocks.BIN_X2_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 2));
		registerBeType(BinBlocks.BIN_X4_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 4));
		registerBeType(BinBlocks.CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 1));
		registerBeType(BinBlocks.CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 2));
		registerBeType(BinBlocks.CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 4));

		registerBeType(TankBlocks.TANK_BLOCK_ENTITY_TYPE, d -> new TankBlockRenderer(d));

		// Generic inference gets confused without
		final ScreenRegistry.Factory<FactilityStorageScreenHandler<DiscreteStorageServerDelegate>, ItemStorageScreen> ITEM_SCREEN_FACTORY = (h, i, t) -> new ItemStorageScreen(h, i, t);
		ScreenRegistry.register(ScreenHandlers.CRATE_BLOCK_TYPE, ITEM_SCREEN_FACTORY);
		ScreenRegistry.register(ScreenHandlers.CRATE_ITEM_TYPE, ITEM_SCREEN_FACTORY);
	}

	private static <E extends BlockEntity> void registerBeType(BlockEntityType<E> type, BlockEntityRendererProvider<E> blockEntityRendererFactory) {
		BlockEntityRendererRegistry.register(type, blockEntityRendererFactory);
		BlockEntityRenderData.registerProvider(type, be -> be);
	}
}
