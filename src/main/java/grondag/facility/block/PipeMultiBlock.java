package grondag.facility.block;

import java.util.function.Function;

import grondag.facility.transport.UniversalTransportBus;
import grondag.facility.wip.transport.impl.AbstractCarrierMultiBlock;
import grondag.facility.wip.transport.impl.SubCarrier;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.base.multiblock.AbstractBlockEntityMember;
import grondag.xm.api.connect.species.SpeciesProperty;

public class PipeMultiBlock extends AbstractCarrierMultiBlock<PipeMultiBlock.Member, PipeMultiBlock> {

	public PipeMultiBlock() {
		super(UniversalTransportBus.BASIC);
	}

	protected static class Member extends AbstractBlockEntityMember<Member, PipeMultiBlock, SubCarrier, PipeBlockEntity> {
		public Member(PipeBlockEntity blockEntity, Function<PipeBlockEntity, SubCarrier> componentFunction) {
			super(blockEntity, componentFunction);
		}

		@Override
		protected void beforeOwnerRemoval() {
			blockEntity.carrier.setParent(null);
		}

		@Override
		protected void afterOwnerAddition() {
			blockEntity.carrier.setParent(owner.carrier);
		}

		protected int species() {
			return blockEntity.getCachedState().get(SpeciesProperty.SPECIES);
		}

		protected boolean canConnect(Member other) {
			return other != null && blockEntity.hasWorld() && other.blockEntity.hasWorld() && species() == other.species();
		}
	}

	protected static final MultiBlockManager<Member, PipeMultiBlock, SubCarrier> DEVICE_MANAGER = MultiBlockManager.create(
			PipeMultiBlock::new, (Member a, Member b) -> a != null && a.canConnect(b));
}
