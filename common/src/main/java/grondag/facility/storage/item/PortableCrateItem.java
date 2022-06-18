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

package grondag.facility.storage.item;

import java.util.List;
import java.util.function.Supplier;

import dev.architectury.registry.menu.MenuRegistry;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import grondag.facility.storage.PortableStore;
import grondag.facility.ux.CrateItemContainerMenu;
import grondag.fluidity.api.storage.Store;

public class PortableCrateItem extends BlockItem {
	public final PortableStore displayCrate;
	protected final Supplier<Store> storeFactory;

	public PortableCrateItem(Block block, Properties settings, Supplier<Store> storeFactory) {
		super(block, settings);
		displayCrate = new PortableStore(storeFactory.get());
		this.storeFactory = storeFactory;
	}

	public PortableStore makeStore(Player player, InteractionHand hand) {
		return new PortableStore(storeFactory.get(), () -> player.getItemInHand(hand), s -> player.setItemInHand(hand, s));
	}

	@Override
	public void fillItemCategory(CreativeModeTab itemGroup, NonNullList<ItemStack> defaultedList) {
		// NOOP
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (!ctx.getPlayer().isShiftKeyDown()) {
			if (use(ctx.getLevel(), ctx.getPlayer(), ctx.getHand()).getResult().consumesAction()) {
				return InteractionResult.SUCCESS;
			}
		}

		return super.useOn(ctx);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
		final ItemStack itemStack = playerEntity.getItemInHand(hand);

		if (itemStack.getItem() != this) {
			return InteractionResultHolder.pass(itemStack);
		}

		if (itemStack.hasTag()) {
			if (!world.isClientSide) {
				// TODO: get the label from BE tags, not currently displayed
				final String label = "todo";
				MenuRegistry.openExtendedMenu((ServerPlayer) playerEntity, new CrateItemContainerMenu.MenuProvider(label, hand));
			}

			return InteractionResultHolder.success(itemStack);
		}

		return InteractionResultHolder.pass(itemStack);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> list, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, list, tooltipContext);
		// TODO: localize
		displayCrate.readFromStack(itemStack);

		if (displayCrate.isEmpty()) {
			list.add(Component.empty());
		} else {
			list.add(Component.literal(Long.toString(displayCrate.count()) + " of " + displayCrate.capacity()));
		}
	}
}
