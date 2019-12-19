package grondag.smart_chest;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

import grondag.smart_chest.screen.SmartChestScreen;

public enum ClientRegistrations {
	;

	static {
		BlockEntityRendererRegistry.INSTANCE.register(Registrations.SMART_CHEST_BLOCK_ENTITY_TYPE, d -> new SmartChestRenderer(d));

		ScreenProviderRegistry.INSTANCE.registerFactory(SmartChestContainer.ID, SmartChestScreen::new);
	}
}
