package grondag.contained.block;

import javax.annotation.Nullable;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import grondag.contained.Contained;
import grondag.fluidity.api.storage.DiscreteStorage;
import grondag.fluidity.api.storage.DiscreteStorageSupplier;
import grondag.fluidity.api.synch.ItemStorageServerDelegate;

public class ItemStorageContainer extends Container implements DiscreteStorageSupplier {
	public static Identifier ID = Contained.REG.id("smart_chest");

	protected final @Nullable DiscreteStorage storage;
	protected String label;
	protected ItemStorageServerDelegate delegate;

	public ItemStorageContainer(PlayerEntity player, int synchId, @Nullable DiscreteStorage storage, String label) {
		super(null, synchId);
		this.storage = storage;
		this.label = label;
		final Inventory inv = player.inventory;

		if(player instanceof ServerPlayerEntity) {
			delegate = new ItemStorageServerDelegate((ServerPlayerEntity) player, storage);
		}

		for(int p = 0; p < 3; ++p) {
			for(int o = 0; o < 9; ++o) {
				addSlot(new Slot(inv, o + p * 9 + 9, o * 18, p * 18));
			}
		}

		for(int p = 0; p < 9; ++p) {
			addSlot(new Slot(inv, p, p * 18, 58));
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	@Nullable
	public DiscreteStorage getDiscreteStorage() {
		return storage;
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		if(delegate != null) {
			delegate.sendUpdates();
		}
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);

		if(delegate != null) {
			delegate.close(playerEntity);
		}
	}
}
