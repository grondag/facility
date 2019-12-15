package grondag.smart_chest;

import net.fabricmc.api.ClientModInitializer;

import grondag.fermion.client.ClientRegistrar;

public class SmartChestClient implements ClientModInitializer {
	public static ClientRegistrar REG  = new ClientRegistrar(SmartChest.MODID);

	@Override
	public void onInitializeClient() {
		ClientRegistrations.values();
	}
}
