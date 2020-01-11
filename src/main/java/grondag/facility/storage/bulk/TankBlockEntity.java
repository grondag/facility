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
package grondag.facility.storage.bulk;

import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import grondag.facility.storage.StorageBlockEntity;
import grondag.facility.storage.StorageClientState;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.multiblock.MultiBlockManager;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;

public class TankBlockEntity extends StorageBlockEntity<StorageClientState<TankBlockEntity>, TankMultiBlock.Member> {
	public TankBlockEntity(BlockEntityType<TankBlockEntity> type, Supplier<Storage> storageSupplier, String labelRoot) {
		super(type, storageSupplier, labelRoot);
	}

	@Override
	protected TankMultiBlock.Member createMember() {
		return new TankMultiBlock.Member(this, b -> b.getInternalStorage());
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected MultiBlockManager deviceManager() {
		return TankMultiBlock.DEVICE_MANAGER;
	}

	@Override
	public Set<ArticleType<?>> articleTypes() {
		return ArticleType.SET_OF_FLUIDS;
	}

	@Override
	protected CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.get(be).applyIfPresent(neighborSide, p ->
		p.attachIfPresent(ArticleType.FLUID, this, ct -> ct.get(this)));
	}

	@Override
	protected StorageClientState<TankBlockEntity> createClientState() {
		return new StorageClientState<>(this);
	}

}
