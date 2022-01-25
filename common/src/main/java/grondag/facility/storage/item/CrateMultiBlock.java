/*
 * This file is part of Facility and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.facility.storage.item;

import java.util.function.Function;

import grondag.facility.init.CrateBlocks;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.multiblock.AbstractBlockEntityMember;
import grondag.fluidity.base.multiblock.AbstractStorageMultiBlock;
import grondag.fluidity.base.storage.discrete.AggregateDiscreteStore;
import grondag.xm.api.connect.species.SpeciesProperty;

public class CrateMultiBlock extends AbstractStorageMultiBlock<CrateMultiBlock.Member, CrateMultiBlock> {
	public CrateMultiBlock() {
		super(new AggregateDiscreteStore().filter(CrateBlocks.FILTER_TYPE_AND_NESTING).typeFilter(ArticleType.ITEM.typePredicate()));
	}

	protected static class Member extends AbstractBlockEntityMember<Member, CrateMultiBlock, Store, CrateBlockEntity> {
		public Member(CrateBlockEntity blockEntity, Function<CrateBlockEntity, Store> componentFunction) {
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
			return blockEntity.getBlockState().getValue(SpeciesProperty.SPECIES);
		}

		protected boolean canConnect(Member other) {
			return other != null && blockEntity.hasLevel() && other.blockEntity.hasLevel() && species() == other.species();
		}
	}

	protected static final MultiBlockManager<Member, CrateMultiBlock, Store> DEVICE_MANAGER = MultiBlockManager.create(
			CrateMultiBlock::new, (Member a, Member b) -> a != null && a.canConnect(b));
}
