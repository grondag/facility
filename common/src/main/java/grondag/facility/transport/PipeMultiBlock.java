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

package grondag.facility.transport;

import java.util.function.Function;

import net.minecraft.world.level.block.entity.BlockEntity;

import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.base.multiblock.AbstractBlockEntityMember;
import grondag.fluidity.wip.api.transport.CarrierType;
import grondag.fluidity.wip.base.transport.AbstractCarrierMultiBlock;
import grondag.fluidity.wip.base.transport.SubCarrier;

public class PipeMultiBlock extends AbstractCarrierMultiBlock<PipeMultiBlock.Member, PipeMultiBlock> {
	public PipeMultiBlock() {
		super(UniversalTransportBus.BASIC);
	}

	@Override
	protected UtbAggregateCarrier createCarrier(CarrierType carrierType) {
		return new UtbAggregateCarrier(carrierType);
	}

	@SuppressWarnings("rawtypes")
	protected static class Member extends AbstractBlockEntityMember<Member, PipeMultiBlock, SubCarrier, PipeBlockEntity> {
		public Member(PipeBlockEntity blockEntity, Function<PipeBlockEntity, SubCarrier> componentFunction) {
			super(blockEntity, componentFunction);
		}

		@Override
		protected void beforeOwnerRemoval() {
			blockEntity.carrier.setParent(null);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void afterOwnerAddition() {
			blockEntity.carrier.setParent(owner.carrier);
		}

		protected boolean canConnect(Member other) {
			final BlockEntity myBe = blockEntity;
			final BlockEntity otherBe = other.blockEntity;

			return myBe.hasLevel() && otherBe.hasLevel()
					&& PipeBlock.canConnectSelf(myBe.getBlockState(), myBe.getBlockPos(), otherBe.getBlockState(), otherBe.getBlockPos());
		}
	}

	@SuppressWarnings("rawtypes")
	protected static final MultiBlockManager<Member, PipeMultiBlock, SubCarrier> DEVICE_MANAGER = MultiBlockManager.create(
			PipeMultiBlock::new, (Member a, Member b) -> a != null && a.canConnect(b));
}
