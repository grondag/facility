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

package grondag.facility.init;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import grondag.facility.Facility;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.facility.storage.item.CrateItemScreenHandler;
import grondag.facility.storage.item.CrateScreenHandler;
import grondag.facility.storage.item.PortableCrateItem;

public abstract class ScreenHandlers {
	private ScreenHandlers() { }

	// TODO: move to own class
	////CRATE BLOCK

	private static MenuType<CrateScreenHandler> crateBlockMenuType;

	public static MenuType<CrateScreenHandler> crateBlockMenuType() {
		return crateBlockMenuType;
	}

	private static CrateScreenHandler clientCrateBlockFactory(int syncId, Inventory inventory, FriendlyByteBuf buf) {
		final String label = buf.readUtf();
		return new CrateScreenHandler(crateBlockMenuType, inventory.player, syncId, null, label);
	}

	private static class CrateBlockScreenHandlerFactory implements ExtendedMenuProvider {
		final String label;
		final BlockPos pos;

		CrateBlockScreenHandlerFactory(String label, BlockPos pos) {
			this.label = label;
			this.pos = pos;
		}

		@Override
		public CrateScreenHandler createMenu(int syncId, Inventory inv, Player player) {
			final Level world = player.getCommandSenderWorld();
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof final CrateBlockEntity myBe) {
				return new CrateScreenHandler(crateBlockMenuType, player, syncId, myBe, label);
			}

			return null;
		}

		@Override
		public Component getDisplayName() {
			return new TextComponent(label);
		}

		@Override
		public void saveExtraData(FriendlyByteBuf buf) {
			buf.writeUtf(label);
		}
	}

	public static ExtendedMenuProvider crateBlockFactory(String label, BlockPos pos) {
		return new CrateBlockScreenHandlerFactory(label, pos);
	}

	// TODO: move to own class
	////  CRATE ITEM

	private static MenuType<CrateItemScreenHandler> crateItemMenuType;

	public static MenuType<CrateItemScreenHandler> crateItemMenuType() {
		return crateItemMenuType;
	}

	private static CrateItemScreenHandler clientCrateItemFactory(int syncId, Inventory inventory, FriendlyByteBuf buf) {
		final InteractionHand hand = InteractionHand.values()[buf.readVarInt()];
		final String label = buf.readUtf();
		return new CrateItemScreenHandler(
				crateItemMenuType,
				inventory.player,
				syncId,
				null,
				label,
				inventory.player.getItemInHand(hand));
	}

	private static class CrateItemScreenHandlerFactory implements ExtendedMenuProvider {
		String label;
		final InteractionHand hand;

		CrateItemScreenHandlerFactory(String label, InteractionHand hand) {
			this.label = label;
			this.hand = hand;
		}

		@Override
		public CrateItemScreenHandler createMenu(int syncId, Inventory inv, Player player) {
			final ItemStack stack = player.getItemInHand(hand);
			final boolean isPortableItem = stack.getItem() instanceof PortableCrateItem;
			return new CrateItemScreenHandler(
					crateItemMenuType,
					player,
					syncId,
					!isPortableItem ? null : ((PortableCrateItem) stack.getItem()).makeStore(player, hand),
							label,
							player.getItemInHand(hand));
		}

		@Override
		public Component getDisplayName() {
			return new TextComponent(label);
		}

		@Override
		public void saveExtraData(FriendlyByteBuf buf) {
			buf.writeVarInt(hand.ordinal());
			buf.writeUtf(label);
		}
	}

	public static ExtendedMenuProvider crateItemFactory(String label, InteractionHand hand) {
		return new CrateItemScreenHandlerFactory(label, hand);
	}

	// TODO: something for tanks?
	//		ScreenHandlerRegistry.registerExtended(TankContainer.ID, (syncId, playerInventory, buf) ->  {
	//			final BlockPos pos = buf.readBlockPos();
	//			final String label = buf.readString();
	//			final World world = playerInventory.player.getEntityWorld();
	//			final BlockEntity be = world.getBlockEntity(pos);
	//
	//			if (be instanceof TankBlockEntity) {
	//				final TankBlockEntity myBe = (TankBlockEntity) be;
	//				return new CrateScreenHandler(playerInventory.player, syncId, world.isClient ? null : Store.STORAGE_COMPONENT.getAccess(myBe).get(), label);
	//			}
	//
	//			return null;
	//		});

	public static void initialize() {
		crateBlockMenuType = Facility.menuType(CrateScreenHandler.ID, MenuRegistry.ofExtended(ScreenHandlers::clientCrateBlockFactory));
		crateItemMenuType = Facility.menuType(CrateItemScreenHandler.ID, MenuRegistry.ofExtended(ScreenHandlers::clientCrateItemFactory));
	}
}
