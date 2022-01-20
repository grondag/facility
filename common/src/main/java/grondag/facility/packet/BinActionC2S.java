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

import dev.architectury.networking.NetworkManager.PacketContext;
import io.netty.buffer.Unpooled;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import grondag.facility.Facility;
import grondag.facility.storage.item.BinBlockEntity;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.StoredArticleView;
import grondag.fluidity.api.storage.FixedStore;

public abstract class BinActionC2S {
	private BinActionC2S() { }

	public static final ResourceLocation ID = Facility.id("bini");

	@Environment(EnvType.CLIENT)
	public static void send(BlockPos pos, int slot, boolean isAttack) {
		if (Minecraft.getInstance().getConnection() != null) {
			final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeBlockPos(pos);
			buf.writeByte(isAttack ? -slot - 1: slot);
			ClientPlayNetworking.send(ID, buf);
		}
	}

	public static void accept(FriendlyByteBuf buf, PacketContext context) {
		final BlockPos pos = buf.readBlockPos();
		final int rawHandle = buf.readByte();
		context.queue(() -> acceptInner(pos, rawHandle, (ServerPlayer) context.getPlayer()));
	}

	private static void acceptInner(BlockPos pos, int rawHandle, ServerPlayer player) {
		final boolean isAttack = rawHandle < 0;
		final int handle = isAttack ? -rawHandle - 1: rawHandle;

		final Level world = player.level;

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
				final int requested = player.isShiftKeyDown() ? 1 : hitResource.toItem().getMaxStackSize();
				final int q = (int) storage.getSupplier().apply(handle, hitResource, requested, false);

				if(q > 0) {
					player.getInventory().placeItemBackInInventory(hitResource.toStack(q));
					player.getInventory().setChanged();
				}
			}
		} else {
			final Article hitResource = view.article();
			final ItemStack stack =  player.getMainHandItem();

			if(stack != null && !stack.isEmpty() && (view.isEmpty() || hitResource.matches(stack))) {
				final int q = (int) storage.getConsumer().apply(handle, stack, false);

				if(q != 0) {
					stack.shrink(q);
					player.setItemInHand(InteractionHand.MAIN_HAND, stack.isEmpty() ? ItemStack.EMPTY : stack);
					player.getInventory().setChanged();
				}
			} else if(!view.isEmpty()) {
				boolean didSucceed = false;
				final NonNullList<ItemStack> main = player.getInventory().items;
				final int limit = main.size();

				for(int i = 0; i < limit; i++) {
					final ItemStack mainStack = main.get(i);

					if(!mainStack.isEmpty() && hitResource.matches(mainStack)) {
						final int q = (int) storage.getConsumer().apply(handle, mainStack, false);

						if(q == 0) {
							break;
						} else {
							didSucceed = true;
							mainStack.shrink(q);

							if(mainStack.isEmpty()) {
								main.set(i, ItemStack.EMPTY);
							}
						}
					}
				}

				final ItemStack offStack = player.getOffhandItem();

				if(!offStack.isEmpty() && hitResource.matches(offStack)) {
					final int q = (int) storage.getConsumer().apply(handle, offStack, false);

					if(q != 0) {
						didSucceed = true;
						offStack.shrink(q);
						if(offStack.isEmpty()) {
							player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
						}
					}
				}

				if(didSucceed) {
					player.getInventory().setChanged();
				}
			}
		}
	}
}
