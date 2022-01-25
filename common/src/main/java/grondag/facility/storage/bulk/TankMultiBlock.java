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

package grondag.facility.storage.bulk;

import java.util.function.Function;

import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.multiblock.AbstractBlockEntityMember;
import grondag.fluidity.base.multiblock.AbstractStorageMultiBlock;
import grondag.fluidity.base.storage.bulk.AggregateBulkStore;
import grondag.xm.api.connect.species.SpeciesProperty;

public class TankMultiBlock extends AbstractStorageMultiBlock<TankMultiBlock.Member, TankMultiBlock> {
	public TankMultiBlock() {
		super(new AggregateBulkStore().filter(ArticleType.FLUID));
	}

	protected static class Member extends AbstractBlockEntityMember<Member, TankMultiBlock, Store, TankBlockEntity> {
		public Member(TankBlockEntity blockEntity, Function<TankBlockEntity, Store> componentFunction) {
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

	protected static final MultiBlockManager<Member, TankMultiBlock, Store> DEVICE_MANAGER = MultiBlockManager.create(
			TankMultiBlock::new, (Member a, Member b) -> a != null && a.canConnect(b));
}
