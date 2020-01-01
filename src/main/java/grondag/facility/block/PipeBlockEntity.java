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
package grondag.facility.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import grondag.facility.transport.UniversalTransportBus;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.base.transport.SingleCarrierProvider;
import grondag.fluidity.wip.base.transport.SubCarrier;

public class PipeBlockEntity extends BlockEntity {
	protected final SubCarrier carrier = new SubCarrier(UniversalTransportBus.BASIC);
	public final CarrierProvider carrierProvider = SingleCarrierProvider.of(carrier);
	protected final PipeMultiBlock.Member member;
	protected PipeMultiBlock owner = null;

	public PipeBlockEntity(BlockEntityType<PipeBlockEntity> type) {
		super(type);
		member = new PipeMultiBlock.Member(this, b -> b.carrier);
	}
}
