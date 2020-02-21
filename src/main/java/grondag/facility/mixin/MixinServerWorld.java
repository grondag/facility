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
package grondag.facility.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

import grondag.facility.block.BlockEntityUnloadCallback;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld {
	// TODO: make this a general-purpose hook for be unloading
	@Inject(at = @At("HEAD"), method = "unloadEntities", cancellable = false)
	private void onUnloadEntities(WorldChunk worldChunk, CallbackInfo ci) {
		for (final BlockEntity be : worldChunk.getBlockEntities().values()) {
			((BlockEntityUnloadCallback) be).onBlockEntityUnloaded();
		}
	}
}
