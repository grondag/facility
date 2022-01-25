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
			buf.writeByte(isAttack ? -slot - 1 : slot);
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
		final int handle = isAttack ? -rawHandle - 1 : rawHandle;

		final Level world = player.level;

		if (world == null) {
			return;
		}

		final BlockEntity be = world.getBlockEntity(pos);

		if (be == null || !(be instanceof BinBlockEntity)) {
			return;
		}

		final FixedStore storage = (FixedStore) ((BinBlockEntity) be).getInternalStorage();
		final StoredArticleView view = storage.view(handle);

		if (view == null) {
			return;
		}

		if (isAttack) {
			if (!view.isEmpty()) {
				final Article hitResource = view.article();
				final int requested = player.isShiftKeyDown() ? 1 : hitResource.toItem().getMaxStackSize();
				final int q = (int) storage.getSupplier().apply(handle, hitResource, requested, false);

				if (q > 0) {
					player.getInventory().placeItemBackInInventory(hitResource.toStack(q));
					player.getInventory().setChanged();
				}
			}
		} else {
			final Article hitResource = view.article();
			final ItemStack stack = player.getMainHandItem();

			if (stack != null && !stack.isEmpty() && (view.isEmpty() || hitResource.matches(stack))) {
				final int q = (int) storage.getConsumer().apply(handle, stack, false);

				if (q != 0) {
					stack.shrink(q);
					player.setItemInHand(InteractionHand.MAIN_HAND, stack.isEmpty() ? ItemStack.EMPTY : stack);
					player.getInventory().setChanged();
				}
			} else if (!view.isEmpty()) {
				boolean didSucceed = false;
				final NonNullList<ItemStack> main = player.getInventory().items;
				final int limit = main.size();

				for (int i = 0; i < limit; i++) {
					final ItemStack mainStack = main.get(i);

					if (!mainStack.isEmpty() && hitResource.matches(mainStack)) {
						final int q = (int) storage.getConsumer().apply(handle, mainStack, false);

						if (q == 0) {
							break;
						} else {
							didSucceed = true;
							mainStack.shrink(q);

							if (mainStack.isEmpty()) {
								main.set(i, ItemStack.EMPTY);
							}
						}
					}
				}

				final ItemStack offStack = player.getOffhandItem();

				if (!offStack.isEmpty() && hitResource.matches(offStack)) {
					final int q = (int) storage.getConsumer().apply(handle, offStack, false);

					if (q != 0) {
						didSucceed = true;
						offStack.shrink(q);

						if (offStack.isEmpty()) {
							player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
						}
					}
				}

				if (didSucceed) {
					player.getInventory().setChanged();
				}
			}
		}
	}
}
