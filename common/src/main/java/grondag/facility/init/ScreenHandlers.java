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

	private static MenuType<CrateScreenHandler> CRATE_BLOCK_TYPE;

	public static MenuType<CrateScreenHandler> CRATE_BLOCK_TYPE() {
		return CRATE_BLOCK_TYPE;
	}

	private static CrateScreenHandler clientCrateBlockFactory(int syncId, Inventory inventory, FriendlyByteBuf buf) {
		final String label = buf.readUtf();
		return new CrateScreenHandler(CRATE_BLOCK_TYPE, inventory.player, syncId, null, label);
	}

	private static class CrateBlockScreenHandlerFactory implements ExtendedMenuProvider  {
	final String label;
	final BlockPos pos;

	public CrateBlockScreenHandlerFactory(String label, BlockPos pos) {
		this.label = label;
		this.pos = pos;
	}

	@Override
	public CrateScreenHandler createMenu(int syncId, Inventory inv, Player player) {
		final Level world = player.getCommandSenderWorld();
		final BlockEntity be = world.getBlockEntity(pos);

		if (be instanceof final CrateBlockEntity myBe) {
			return new CrateScreenHandler(CRATE_BLOCK_TYPE, player, syncId, myBe, label);
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

	private static MenuType<CrateItemScreenHandler> CRATE_ITEM_TYPE;

	public static MenuType<CrateItemScreenHandler> CRATE_ITEM_TYPE() {
		return CRATE_ITEM_TYPE;
	}

	private static CrateItemScreenHandler clientCrateItemFactory(int syncId, Inventory inventory, FriendlyByteBuf buf) {
		final InteractionHand hand = InteractionHand.values()[buf.readVarInt()];
		final String label = buf.readUtf();
		return new CrateItemScreenHandler(
				CRATE_ITEM_TYPE,
				inventory.player,
				syncId,
				null,
				label,
				inventory.player.getItemInHand(hand));
	}

	private static class CrateItemScreenHandlerFactory implements ExtendedMenuProvider  {
		String label;
		final InteractionHand hand;

		public CrateItemScreenHandlerFactory(String label, InteractionHand hand) {
			this.label = label;
			this.hand = hand;
		}

		@Override
		public CrateItemScreenHandler createMenu(int syncId, Inventory inv, Player player) {
			final ItemStack stack  = player.getItemInHand(hand);
			final boolean isPortableItem = stack.getItem() instanceof PortableCrateItem;
			return new CrateItemScreenHandler(
					CRATE_ITEM_TYPE,
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
		CRATE_BLOCK_TYPE = Facility.menuType(CrateScreenHandler.ID, MenuRegistry.ofExtended(ScreenHandlers::clientCrateBlockFactory));
		CRATE_ITEM_TYPE =  Facility.menuType(CrateItemScreenHandler.ID, MenuRegistry.ofExtended(ScreenHandlers::clientCrateItemFactory));
	}
}
