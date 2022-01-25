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

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.facility.storage.StorageBlockEntity;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.base.storage.AbstractStore;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;

public class CrateBlockEntity extends StorageBlockEntity<CrateClientState, CrateMultiBlock.Member> {
	public CrateBlockEntity(BlockEntityType<? extends CrateBlockEntity> type, BlockPos pos, BlockState state, @SuppressWarnings("rawtypes") Supplier<AbstractStore> storageSupplier, String labelRoot) {
		super(type, pos, state, storageSupplier, labelRoot);
	}

	@Override
	protected CrateMultiBlock.Member createMember() {
		return new CrateMultiBlock.Member(this, b -> b.getInternalStorage());
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected MultiBlockManager deviceManager() {
		return CrateMultiBlock.DEVICE_MANAGER;
	}

	@Override
	protected CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.getAccess(be).applyIfPresent(neighborSide, p ->
		p.attachIfPresent(ArticleType.ITEM, ct -> ct.getAccess(this)));
	}

	@Override
	protected CrateClientState createClientState() {
		return new CrateClientState(this);
	}
}
