package grondag.facility.client;

import net.fabricmc.api.ClientModInitializer;

import grondag.facility.Facility;
import grondag.fermion.client.ClientRegistrar;

public class FacilityClient implements ClientModInitializer {
	public static ClientRegistrar REG  = new ClientRegistrar(Facility.MODID);

	@Override
	public void onInitializeClient() {
		ClientRegistrations.values();
	}
}
