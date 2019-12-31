package grondag.facility.block;

import java.util.function.Function;

import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.multiblock.AbstractBlockEntityMember;
import grondag.fluidity.base.multiblock.AbstractStorageMultiBlock;
import grondag.fluidity.base.storage.discrete.AggregateDiscreteStorage;
import grondag.xm.api.connect.species.SpeciesProperty;

public class ItemStorageMultiBlock extends AbstractStorageMultiBlock<ItemStorageMultiBlock.Member, ItemStorageMultiBlock> {
	public ItemStorageMultiBlock() {
		super(new AggregateDiscreteStorage());
	}

	protected static class Member extends AbstractBlockEntityMember<Member, ItemStorageMultiBlock, Storage, ItemStorageBlockEntity> {
		public Member(ItemStorageBlockEntity blockEntity, Function<ItemStorageBlockEntity, Storage> componentFunction) {
			super(blockEntity, componentFunction);
		}

		@Override
		protected void beforeOwnerRemoval() {
			blockEntity.wrapper.setWrapped(blockEntity.getInternalStorage());
		}

		@Override
		protected void afterOwnerAddition() {
			blockEntity.wrapper.setWrapped(owner.storage);
		}

		protected int species() {
			return blockEntity.getCachedState().get(SpeciesProperty.SPECIES);
		}

		protected boolean canConnect(Member other) {
			return other != null && blockEntity.hasWorld() && other.blockEntity.hasWorld() && species() == other.species();
		}
	}

	protected static final MultiBlockManager<Member, ItemStorageMultiBlock, Storage> DEVICE_MANAGER = MultiBlockManager.create(
			ItemStorageMultiBlock::new, (Member a, Member b) -> a != null && a.canConnect(b));
}
