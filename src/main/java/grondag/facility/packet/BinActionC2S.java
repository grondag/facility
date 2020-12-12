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
package grondag.facility.packet;

import io.netty.buffer.Unpooled;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

import grondag.facility.Facility;
import grondag.facility.storage.item.BinBlockEntity;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.FixedStore;

public enum BinActionC2S {
	;

	public static final Identifier ID = Facility.REG.id("bini");

	@Environment(EnvType.CLIENT)
	public static void send(BlockPos pos, int slot, boolean isAttack) {
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBlockPos(pos);
			buf.writeByte(isAttack ? -slot - 1: slot);
			ClientPlayNetworking.send(ID, buf);
		}
	}

	public static void accept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		final BlockPos pos = buf.readBlockPos();
		final int rawHandle = buf.readByte();


		if (server.isOnThread()) {
			acceptInner(pos, rawHandle, player);
		} else {
			server.execute(() -> acceptInner(pos, rawHandle, player));
		}
	}

	private static void acceptInner(BlockPos pos, int rawHandle, ServerPlayerEntity player) {
		final boolean isAttack = rawHandle < 0;
		final int handle = isAttack ? -rawHandle - 1: rawHandle;

		final World world = player.world;

		if(world == null) {
			return;
		}

		final BlockEntity be = world.getBlockEntity(pos);

		if(be == null || !(be instanceof BinBlockEntity)) {
			return;
		}

		final FixedStore storage = (FixedStore) ((BinBlockEntity) be).getInternalStorage();
		final StoredArticleView view = storage.view(handle);

		if(view == null) {
			return;
		}

		if(isAttack) {
			if(!view.isEmpty()) {
				final Article hitResource = view.article();
				final int requested = player.isSneaking() ? 1 : hitResource.toItem().getMaxCount();
				final int q = (int) storage.getSupplier().apply(handle, hitResource, requested, false);

				if(q > 0) {
					player.inventory.offerOrDrop(world, hitResource.toStack(q));
					player.inventory.markDirty();
				}
			}
		} else {
			final Article hitResource = view.article();
			final ItemStack stack =  player.getMainHandStack();

			if(stack != null && !stack.isEmpty() && (view.isEmpty() || hitResource.matches(stack))) {
				final int q = (int) storage.getConsumer().apply(handle, stack, false);

				if(q != 0) {
					stack.decrement(q);
					player.setStackInHand(Hand.MAIN_HAND, stack.isEmpty() ? ItemStack.EMPTY : stack);
					player.inventory.markDirty();
				}
			} else if(!view.isEmpty()) {
				boolean didSucceed = false;
				final DefaultedList<ItemStack> main = player.inventory.main;
				final int limit = main.size();

				for(int i = 0; i < limit; i++) {
					final ItemStack mainStack = main.get(i);

					if(!mainStack.isEmpty() && hitResource.matches(mainStack)) {
						final int q = (int) storage.getConsumer().apply(handle, mainStack, false);

						if(q == 0) {
							break;
						} else {
							didSucceed = true;
							mainStack.decrement(q);

							if(mainStack.isEmpty()) {
								main.set(i, ItemStack.EMPTY);
							}
						}
					}
				}

				final ItemStack offStack = player.getOffHandStack();

				if(!offStack.isEmpty() && hitResource.matches(offStack)) {
					final int q = (int) storage.getConsumer().apply(handle, offStack, false);

					if(q != 0) {
						didSucceed = true;
						offStack.decrement(q);
						if(offStack.isEmpty()) {
							player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
						}
					}
				}

				if(didSucceed) {
					player.inventory.markDirty();
				}
			}
		}
	}
}
