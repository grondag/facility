package grondag.contained.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import grondag.contained.Registrations;
import grondag.fluidity.api.storage.DiscreteStorage;
import grondag.fluidity.api.storage.DiscreteStorageSupplier;

public class ItemStorageBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity, DiscreteStorageSupplier, Tickable {

	protected DiscreteStorage storage;
	public String label = "SmartChest 2000";

	public ItemStorageBlockEntity() {
		super(Registrations.ITEM_STORAGE_BLOCK_ENTITY_TYPE);
	}

	@Override
	public Object getRenderAttachmentData() {
		return this;
	}

	@Override
	public DiscreteStorage getDiscreteStorage() {
		DiscreteStorage result = storage;

		if(result == null) {
			final Block block = getCachedState().getBlock();

			if(block instanceof ItemStorageBlock) {
				result = ((ItemStorageBlock) block).storageFactory.get();
				storage = result;
			}
		}

		return result;
	}

	@Override
	public void tick() {
		//		final DiscreteStorage storage = getDiscreteStorage();
		//
		//		if(storage == null) {
		//			return;
		//		}
		//
		//		if(storage.count() >= storage.capacity()) {
		//			storage.clear();
		//		} else if(storage instanceof SimpleItemStorage){
		//			final SimpleItemStorage myStorage = (SimpleItemStorage) storage;
		//			final ThreadLocalRandom rand = ThreadLocalRandom.current();
		//
		//			final ItemStack stack = myStorage.getInvStack(rand.nextInt(myStorage.getInvSize()));
		//
		//			if(stack.isEmpty()) {
		//				final Item item = Registry.ITEM.getRandom(rand);
		//				storage.accept(item, 1, false);
		//			} else {
		//				storage.accept(stack.getItem(), 1, false);
		//			}
		//		} else {
		//			final ThreadLocalRandom rand = ThreadLocalRandom.current();
		//			final int handle = rand.nextInt(200);
		//
		//			if(handle < storage.handleCount()) {
		//				storage.accept(storage.view(handle).item(), 1, false);
		//			} else {
		//				final Item item = Registry.ITEM.getRandom(rand);
		//				storage.accept(item, 1, false);
		//			}
		//		}
	}
}
