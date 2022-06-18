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

package grondag.facility.ux;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import grondag.facility.Facility;
import grondag.facility.init.MenuTypes;
import grondag.facility.storage.StorageBlockEntity;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;

public class CrateContainerMenu extends FacilityBaseContainerMenu<DiscreteStorageServerDelegate> {
	public static ResourceLocation ID = Facility.id("crate");
	protected final StorageBlockEntity<?, ?> storageBe;

	/** BlockEntity is null on client. */
	public CrateContainerMenu(MenuType<?> type, Player player, int synchId, @Nullable StorageBlockEntity<?, ?> storageBe, String label) {
		super(type, player, synchId, storageBe == null ? null : storageBe.getEffectiveStorage(), label);
		this.storageBe = storageBe;
	}

	@Override
	public boolean stillValid(Player playerEntity) {
		return storageBe == null || !storageBe.isRemoved();
	}

	@Override
	protected DiscreteStorageServerDelegate createDelegate(ServerPlayer player, Store storage) {
		return new DiscreteStorageServerDelegate(player, storage);
	}

	public static CrateContainerMenu createFromPacket(int syncId, Inventory inventory, FriendlyByteBuf buf) {
		final String label = buf.readUtf();
		return new CrateContainerMenu(MenuTypes.crateBlockMenuType(), inventory.player, syncId, null, label);
	}

	public static class MenuProvider implements ExtendedMenuProvider {
		final String label;
		final BlockPos pos;

		public MenuProvider(String label, BlockPos pos) {
			this.label = label;
			this.pos = pos;
		}

		@Override
		public CrateContainerMenu createMenu(int syncId, Inventory inv, Player player) {
			final Level world = player.getCommandSenderWorld();
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof final CrateBlockEntity myBe) {
				return new CrateContainerMenu(MenuTypes.crateBlockMenuType(), player, syncId, myBe, label);
			}

			return null;
		}

		@Override
		public Component getDisplayName() {
			return Component.literal(label);
		}

		@Override
		public void saveExtraData(FriendlyByteBuf buf) {
			buf.writeUtf(label);
		}
	}
}
