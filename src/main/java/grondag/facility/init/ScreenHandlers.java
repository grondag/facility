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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;

import grondag.facility.storage.item.CrateBlockEntity;
import grondag.facility.storage.item.CrateItemScreenHandler;
import grondag.facility.storage.item.CrateScreenHandler;
import grondag.facility.storage.item.PortableCrateItem;

public enum ScreenHandlers {
	;

	////CRATE BLOCK

	public static final ScreenHandlerType<CrateScreenHandler> CRATE_BLOCK_TYPE = ScreenHandlerRegistry.registerExtended(CrateScreenHandler.ID, ScreenHandlers::clientCrateBlockFactory);

	private static CrateScreenHandler clientCrateBlockFactory(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
		final String label = buf.readString();
		return new CrateScreenHandler(CRATE_BLOCK_TYPE, inventory.player, syncId, null, label);
	}

	private static class CrateBlockScreenHandlerFactory implements ExtendedScreenHandlerFactory  {
		final String label;
		final BlockPos pos;

		public CrateBlockScreenHandlerFactory(String label, BlockPos pos) {
			this.label = label;
			this.pos = pos;
		}

		@Override
		public CrateScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
			final World world = player.getEntityWorld();
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof CrateBlockEntity) {
				final CrateBlockEntity myBe = (CrateBlockEntity) be;
				return new CrateScreenHandler(CRATE_BLOCK_TYPE, player, syncId, myBe, label);
			}

			return null;
		}

		@Override
		public Text getDisplayName() {
			return new LiteralText(label);
		}

		@Override
		public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
			buf.writeString(label);
		}
	}

	public static ExtendedScreenHandlerFactory crateBlockFactory(String label, BlockPos pos) {
		return new CrateBlockScreenHandlerFactory(label, pos);
	}

	////  CRATE ITEM

	public static final ScreenHandlerType<CrateItemScreenHandler> CRATE_ITEM_TYPE = ScreenHandlerRegistry.registerExtended(CrateItemScreenHandler.ID, ScreenHandlers::clientCrateItemFactory);

	private static CrateItemScreenHandler clientCrateItemFactory(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
		final Hand hand = Hand.values()[buf.readVarInt()];
		final String label = buf.readString();
		return new CrateItemScreenHandler(
				CRATE_ITEM_TYPE,
				inventory.player,
				syncId,
				null,
				label,
				inventory.player.getStackInHand(hand));
	}

	private static class CrateItemScreenHandlerFactory implements ExtendedScreenHandlerFactory  {
		String label;
		final Hand hand;

		public CrateItemScreenHandlerFactory(String label, Hand hand) {
			this.label = label;
			this.hand = hand;
		}

		@Override
		public CrateItemScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
			final ItemStack stack  = player.getStackInHand(hand);
			final boolean isPortableItem = stack.getItem() instanceof PortableCrateItem;
			return new CrateItemScreenHandler(
					CRATE_ITEM_TYPE,
					player,
					syncId,
					!isPortableItem ? null : ((PortableCrateItem) stack.getItem()).makeStore(player, hand),
							label,
							player.getStackInHand(hand));
		}

		@Override
		public Text getDisplayName() {
			return new LiteralText(label);
		}

		@Override
		public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
			buf.writeVarInt(hand.ordinal());
			buf.writeString(label);
		}
	}

	public static ExtendedScreenHandlerFactory crateItemFactory(String label, Hand hand) {
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
}
