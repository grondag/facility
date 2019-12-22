package grondag.contained.client;

import net.fabricmc.api.ClientModInitializer;

import grondag.contained.Contained;
import grondag.fermion.client.ClientRegistrar;

public class ContainedClient implements ClientModInitializer {
	public static ClientRegistrar REG  = new ClientRegistrar(Contained.MODID);

	@Override
	public void onInitializeClient() {
		ClientRegistrations.values();
	}
}
