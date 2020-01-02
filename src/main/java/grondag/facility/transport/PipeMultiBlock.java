/*******************************************************************************
 * Copyright 2019, 2020 grondag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.facility.transport;

import java.util.function.Function;

import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.base.multiblock.AbstractBlockEntityMember;
import grondag.fluidity.wip.base.transport.AbstractCarrierMultiBlock;
import grondag.fluidity.wip.base.transport.SubCarrier;
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
