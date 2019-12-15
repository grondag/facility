package grondag.smart_chest;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public enum ClientRegistrations {
	;

	static {
		BlockEntityRendererRegistry.INSTANCE.register(Registrations.SMART_CHEST_BLOCK_ENTITY_TYPE, d -> new SmartChestRenderer(d));
	}
}
