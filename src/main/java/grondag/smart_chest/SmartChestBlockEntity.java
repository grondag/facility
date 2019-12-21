package grondag.smart_chest;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Tickable;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.fluidity.api.article.DiscreteArticleView;
import grondag.fluidity.api.storage.DiscreteStorage;
import grondag.fluidity.api.storage.DiscreteStorageSupplier;
import grondag.fluidity.base.storage.SimpleItemStorage;

public class SmartChestBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity, DiscreteStorageSupplier, Tickable {

	protected DiscreteStorage storage = new SimpleItemStorage(32);
	protected String label = "SmartChest 2000";

	public SmartChestBlockEntity() {
		super(Registrations.SMART_CHEST_BLOCK_ENTITY_TYPE);
	}

	@Override
	public Object getRenderAttachmentData() {
		return this;
	}

	@Override
	public DiscreteStorage getDiscreteStorage() {
		return storage;
	}

	@Override
	public void tick() {
		if(storage.count() >= storage.capacity()) {
			storage.clear();
		} else {
			final ThreadLocalRandom rand = ThreadLocalRandom.current();

			final DiscreteArticleView view = storage.view(rand.nextInt(storage.slotCount()));

			if(view.isEmpty()) {
				final Item item = Registry.ITEM.getRandom(rand);
				storage.accept(item, 1, false);
			} else {
				storage.accept(view.item(), 1, false);
			}
		}
	}
}
