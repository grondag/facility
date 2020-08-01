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

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

import grondag.facility.init.BinBlocks;
import grondag.facility.init.CrateBlocks;
import grondag.facility.init.ScreenHandlers;
import grondag.facility.init.TankBlocks;
import grondag.facility.storage.item.CrateScreenHandler;

public enum ClientRegistrations {
	;

	static {
		BlockEntityRendererRegistry.INSTANCE.register(CrateBlocks.SLOTTED_CRATE_BLOCK_ENTITY_TYPE, d -> new StorageBlockRenderer<>(d));
		BlockEntityRendererRegistry.INSTANCE.register(CrateBlocks.CRATE_BLOCK_ENTITY_TYPE, d -> new StorageBlockRenderer<>(d));
		BlockEntityRendererRegistry.INSTANCE.register(BinBlocks.BIN_X1_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 1));
		BlockEntityRendererRegistry.INSTANCE.register(BinBlocks.BIN_X2_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 2));
		BlockEntityRendererRegistry.INSTANCE.register(BinBlocks.BIN_X4_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 4));
		BlockEntityRendererRegistry.INSTANCE.register(BinBlocks.CREATIVE_BIN_X1_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 1));
		BlockEntityRendererRegistry.INSTANCE.register(BinBlocks.CREATIVE_BIN_X2_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 2));
		BlockEntityRendererRegistry.INSTANCE.register(BinBlocks.CREATIVE_BIN_X4_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 4));

		BlockEntityRendererRegistry.INSTANCE.register(TankBlocks.TANK_BLOCK_ENTITY_TYPE, d -> new TankBlockRenderer(d));

		// Generic inference gets confused without
		final ScreenRegistry.Factory<CrateScreenHandler, ItemStorageScreen> ITEM_SCREEN_FACTORY = (h, i, t) -> new ItemStorageScreen(h, i, t);

		ScreenRegistry.register(ScreenHandlers.CRATE_BLOCK_TYPE, ITEM_SCREEN_FACTORY);
		ScreenRegistry.register(ScreenHandlers.CRATE_ITEM_TYPE, ITEM_SCREEN_FACTORY);
	}
}
