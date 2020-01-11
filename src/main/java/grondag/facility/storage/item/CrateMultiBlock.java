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
package grondag.facility.storage.item;

import java.util.function.Function;

import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.multiblock.AbstractBlockEntityMember;
import grondag.fluidity.base.multiblock.AbstractStorageMultiBlock;
import grondag.fluidity.base.storage.discrete.AggregateDiscreteStorage;
import grondag.xm.api.connect.species.SpeciesProperty;

public class CrateMultiBlock extends AbstractStorageMultiBlock<CrateMultiBlock.Member, CrateMultiBlock> {
	public CrateMultiBlock() {
		super(new AggregateDiscreteStorage());
	}

	protected static class Member extends AbstractBlockEntityMember<Member, CrateMultiBlock, Storage, CrateBlockEntity> {
		public Member(CrateBlockEntity blockEntity, Function<CrateBlockEntity, Storage> componentFunction) {
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

	protected static final MultiBlockManager<Member, CrateMultiBlock, Storage> DEVICE_MANAGER = MultiBlockManager.create(
			CrateMultiBlock::new, (Member a, Member b) -> a != null && a.canConnect(b));
}
