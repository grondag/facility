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
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

import grondag.facility.init.BinBlocks;
import grondag.facility.init.CrateBlocks;
import grondag.facility.storage.item.CrateContainer;

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

		ScreenProviderRegistry.INSTANCE.registerFactory(CrateContainer.ID, ItemStorageScreen::new);
	}
}
