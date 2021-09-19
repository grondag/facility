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

import grondag.facility.storage.TrackedBlockEntity;
import grondag.fermion.world.WorldTaskManager;
import grondag.fluidity.api.device.BlockComponentContext;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.base.transport.SingleCarrierProvider;
import grondag.fluidity.wip.base.transport.SubCarrier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PipeBlockEntity extends TrackedBlockEntity {
	protected final SubCarrier<UtbCostFunction> carrier = new UtbSubCarrier(UniversalTransportBus.BASIC);
	public final CarrierProvider carrierProvider;
	protected final PipeMultiBlock.Member member;
	protected PipeMultiBlock owner = null;
	protected boolean isEnqued = false;
	protected boolean isRegistered = false;

	public PipeBlockEntity(BlockEntityType<? extends PipeBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		member = new PipeMultiBlock.Member(this, b -> b.carrier);
		carrierProvider = createCarrierProvider();
	}

	protected CarrierProvider createCarrierProvider() {
		return SingleCarrierProvider.of(carrier);
	}

	protected final void enqueUpdate() {
		if(!isEnqued && !level.isClientSide) {
			isEnqued = true;
			WorldTaskManager.enqueueImmediate(this::enquedUpdate);
		}
	}

	protected void onEnquedUpdate() {

	}

	public final void enquedUpdate() {
		if(level == null || level.isClientSide) {
			return;
		}

		onEnquedUpdate();
		isEnqued = false;
	}

	@Override
	public final void onLoaded() {
		if(!isRegistered && hasLevel() && !level.isClientSide) {
			PipeMultiBlock.DEVICE_MANAGER.connect(member);
			isRegistered = true;
			enqueUpdate();
		} else {
			assert false : "detected duplicate loading.";
		}
	}

	@Override
	public final void onUnloaded() {
		if(isRegistered && hasLevel() && !level.isClientSide) {
			PipeMultiBlock.DEVICE_MANAGER.disconnect(member);
			isRegistered = false;
		} else {
			assert false : "detected incorrected unloading.";
		}
	}

	public CarrierProvider getCarrierProvider(BlockComponentContext ctx) {
		return carrierProvider;
	}
}
