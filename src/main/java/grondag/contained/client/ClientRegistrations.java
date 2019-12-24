package grondag.contained.client;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

import grondag.contained.Registrations;
import grondag.contained.block.ItemStorageContainer;

public enum ClientRegistrations {
	;

	static {
		BlockEntityRendererRegistry.INSTANCE.register(Registrations.BARREL_BLOCK_ENTITY_TYPE, d -> new StorageBlockRenderer<>(d));
		BlockEntityRendererRegistry.INSTANCE.register(Registrations.CRATE_BLOCK_ENTITY_TYPE, d -> new StorageBlockRenderer<>(d));
		BlockEntityRendererRegistry.INSTANCE.register(Registrations.BIN_X1_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 1));
		BlockEntityRendererRegistry.INSTANCE.register(Registrations.BIN_X2_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 2));
		BlockEntityRendererRegistry.INSTANCE.register(Registrations.BIN_X4_BLOCK_ENTITY_TYPE, d -> new BinBlockRenderer(d, 4));

		ScreenProviderRegistry.INSTANCE.registerFactory(ItemStorageContainer.ID, ItemStorageScreen::new);
	}
}
