package grondag.smart_chest;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.ImmutableList;
import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

import grondag.fermion.client.ClientRegistrar;
import grondag.smart_chest.delegate.ItemDelegate;
import grondag.smart_chest.delegate.ItemStorageClientDelegate;

public class SmartChestClient implements ClientModInitializer {
	public static ClientRegistrar REG  = new ClientRegistrar(SmartChest.MODID);

	private static final ArrayList<ItemDelegate> DUMMY  = new ArrayList<>();
	int rawId = 0;

	@Override
	public void onInitializeClient() {
		ClientRegistrations.values();

		//TODO: remove
		ItemStorageClientDelegate.handleStorageRefresh(Collections.emptyList(), 2000, true);

		ClientTickCallback.EVENT.register(c -> {
			if(ItemStorageClientDelegate.usedCapacity() == 2000) {
				ItemStorageClientDelegate.handleStorageRefresh(Collections.emptyList(), 2000, true);
				DUMMY.clear();
			} else {
				final ThreadLocalRandom rand = ThreadLocalRandom.current();
				final int size = DUMMY.size();

				if(size > 0 && rand.nextInt(200) < size) {
					final ItemDelegate d = DUMMY.get(rand.nextInt(size));
					d.count++;
					ItemStorageClientDelegate.handleStorageRefresh(ImmutableList.of(d.clone()), 2000, false);
				} else {
					final ItemDelegate d = new ItemDelegate(Registry.ITEM.getRandom(rand).getStackForRender(), 1, size);
					DUMMY.add(d);
					ItemStorageClientDelegate.handleStorageRefresh(ImmutableList.of(d.clone()), 2000, false);
				}
			}
		});
	}
}
