package grondag.contained.client;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

import grondag.contained.Registrations;
import grondag.contained.block.ItemStorageContainer;

public enum ClientRegistrations {
	;

	static {
		BlockEntityRendererRegistry.INSTANCE.register(Registrations.SMART_CHEST_BLOCK_ENTITY_TYPE, d -> new StorageBlockRenderer(d));

		ScreenProviderRegistry.INSTANCE.registerFactory(ItemStorageContainer.ID, ItemStorageScreen::new);
	}
}
