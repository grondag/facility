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
package grondag.facility.init;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import grondag.facility.storage.bulk.TankBlockEntity;
import grondag.facility.storage.bulk.TankContainer;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.facility.storage.item.CrateContainer;
import grondag.facility.storage.item.PortableCrateItem;
import grondag.fluidity.api.storage.Store;

public enum Containers {
	;

	static {
		ContainerProviderRegistry.INSTANCE.registerFactory(CrateContainer.ID, (syncId, identifier, player, buf) ->  {
			final BlockPos pos = buf.readBlockPos();
			final String label = buf.readString(1024);
			final World world = player.getEntityWorld();
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof CrateBlockEntity) {
				final CrateBlockEntity myBe = (CrateBlockEntity) be;
				return new CrateContainer(player, syncId, world.isClient ? null : Store.STORAGE_COMPONENT.getAccess(myBe).get(), label);
			}

			return null;
		});

		ContainerProviderRegistry.INSTANCE.registerFactory(CrateContainer.ID_ITEM, (syncId, identifier, player, buf) ->  {
			final Hand hand = Hand.values()[buf.readVarInt()];
			final String label = buf.readString(1024);
			final World world = player.getEntityWorld();
			final ItemStack stack  = player.getStackInHand(hand);
			final boolean isPortableItem = stack.getItem() instanceof PortableCrateItem;
			return new CrateContainer(
					player,
					syncId,
					world.isClient || !isPortableItem ? null : ((PortableCrateItem) stack.getItem()).makeStore(player, hand),
							label,
							player.getStackInHand(hand));
		});

		ContainerProviderRegistry.INSTANCE.registerFactory(TankContainer.ID, (syncId, identifier, player, buf) ->  {
			final BlockPos pos = buf.readBlockPos();
			final String label = buf.readString(1024);
			final World world = player.getEntityWorld();
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof TankBlockEntity) {
				final TankBlockEntity myBe = (TankBlockEntity) be;
				return new CrateContainer(player, syncId, world.isClient ? null : Store.STORAGE_COMPONENT.getAccess(myBe).get(), label);
			}

			return null;
		});
	}
}
