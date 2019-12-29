/*******************************************************************************
 * Copyright 2019 grondag
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
package grondag.facility.wip.transport.impl;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import grondag.fluidity.api.device.CompoundDevice;
import grondag.fluidity.api.device.CompoundMemberDevice;
import grondag.fluidity.api.storage.Storage;

@API(status = Status.EXPERIMENTAL)
public abstract class CompoundCarrierDevice<T extends CompoundMemberDevice<T, U>, U extends CompoundCarrierDevice<T, U>> implements CompoundDevice<T, U> {

	protected final ObjectOpenHashSet<T> devices = new ObjectOpenHashSet<>();

	@Override
	public void add(T device) {
		devices.add(device);
	}

	@Override
	public void remove(T device) {
		onRemove(device);
		devices.remove(device);
	}

	protected abstract void onRemove(T device);

	@Override
	public int deviceCount() {
		return devices.size();
	}

	@Override
	public Storage getStorage(Direction side, Identifier id) {
		return null;
	}

	@Override
	public boolean hasStorage(Direction side, Identifier id) {
		return false;
	}

	@Override
	public void removalAllAndClose(Consumer<T> closeAction) {
		devices.forEach(d -> {
			onRemove(d);
			closeAction.accept(d);
		});

		devices.clear();
		close();
	}
}
