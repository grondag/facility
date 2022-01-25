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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.facility.varia.WorldTaskManager;
import grondag.fluidity.api.device.BlockComponentContext;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.base.transport.SingleCarrierProvider;
import grondag.fluidity.wip.base.transport.SubCarrier;

public class PipeBlockEntity extends BlockEntity {
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
		if (!isEnqued && !level.isClientSide) {
			isEnqued = true;
			WorldTaskManager.enqueueImmediate(this::enquedUpdate);
		}
	}

	protected void onEnquedUpdate() {
		// NOOP
	}

	public final void enquedUpdate() {
		if (level == null || level.isClientSide) {
			return;
		}

		onEnquedUpdate();
		isEnqued = false;
	}

	@Override
	public void setLevel(Level world) {
		super.setLevel(world);
		onLoaded();
		enqueUpdate();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		onUnloaded();
		enqueUpdate();
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved();
		onLoaded();
		enqueUpdate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		enqueUpdate();
	}

	private void onLoaded() {
		if (!isRegistered && hasLevel() && !level.isClientSide) {
			PipeMultiBlock.DEVICE_MANAGER.connect(member);
			isRegistered = true;
			enqueUpdate();
		} else {
			assert false : "detected duplicate loading.";
		}
	}

	private void onUnloaded() {
		if (isRegistered && hasLevel() && !level.isClientSide) {
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
