package grondag.smart_chest;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.ImmutableList;
import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

import grondag.fermion.client.ClientRegistrar;
import grondag.fluidity.api.client.ItemStorageClientDelegate;
import grondag.fluidity.impl.ItemDisplayDelegateImpl;

public class SmartChestClient implements ClientModInitializer {
	public static ClientRegistrar REG  = new ClientRegistrar(SmartChest.MODID);

	private static final ArrayList<ItemDisplayDelegateImpl> DUMMY  = new ArrayList<>();
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
					final ItemDisplayDelegateImpl d = DUMMY.get(rand.nextInt(size));
					d.set(d.displayStack(), d.count() + 1, d.handle());
					ItemStorageClientDelegate.handleStorageRefresh(ImmutableList.of(d.clone()), 2000, false);
				} else {
					final ItemDisplayDelegateImpl d = new ItemDisplayDelegateImpl(Registry.ITEM.getRandom(rand).getStackForRender(), 1, size);
					DUMMY.add(d);
					ItemStorageClientDelegate.handleStorageRefresh(ImmutableList.of(d.clone()), 2000, false);
				}
			}
		});
	}
}
