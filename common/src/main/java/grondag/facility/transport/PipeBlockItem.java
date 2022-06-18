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

package grondag.facility.transport;

import java.util.function.BiFunction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import grondag.facility.transport.model.BasePipeModel;
import grondag.xm.api.block.XmBlockState;
import grondag.xm.api.modelstate.MutableModelState;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;

public class PipeBlockItem extends BlockItem {
	public static final int AUTO_SELECT_SPECIES = -1;

	private static final String SPECIES = "species";

	public PipeBlockItem(Block block, Properties settings) {
		super(block, settings);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
		if (world.isClientSide) {
			return super.use(world, playerEntity, hand);
		} else {
			final ItemStack itemStack = playerEntity.getItemInHand(hand);
			itemStack.setTag(cycleTag(itemStack, playerEntity.isShiftKeyDown()));
			playerEntity.setItemInHand(hand, itemStack);

			final int spec = species(itemStack);
			final Component msg = spec == AUTO_SELECT_SPECIES ? Component.translatable("transport.facility.circuit.auto") : Component.translatable("transport.facility.circuit.num", spec);
			playerEntity.displayClientMessage(msg, true);
			return InteractionResultHolder.success(itemStack);
		}
	}

	private static CompoundTag cycleTag(ItemStack itemStack, boolean reverse) {
		CompoundTag tag = itemStack.getTag();

		if (tag == null) {
			tag = new CompoundTag();
			tag.putInt(SPECIES, 0);
		} else if (tag.contains(SPECIES)) {
			// has species, so increment
			final int species = tag.getInt(SPECIES) + (reverse ? -1 : 1);

			// reset to auto on loop
			if (species > 15 || species < 0) {
				tag.remove(SPECIES);

				if (tag.isEmpty()) {
					tag = null;
				}
			} else {
				tag.putInt(SPECIES, species);
			}
		} else {
			// has tag (wierd) but no species, so start cycle
			tag.putInt(SPECIES, 0);
		}

		return tag;
	}

	public static int species(ItemStack itemStack) {
		final CompoundTag tag = itemStack.getTag();

		if (tag == null || !tag.contains(SPECIES)) {
			return AUTO_SELECT_SPECIES;
		} else {
			return tag.getInt(SPECIES);
		}
	}

	public static final BiFunction<ItemStack, Level, MutableModelState> PIPE_ITEM_MODEL_FUNCTION = (stack, world) -> {
		MutablePrimitiveState result = null;

		if (stack.getItem() instanceof PipeBlockItem) {
			final Block block = ((BlockItem) stack.getItem()).getBlock();

			result = XmBlockState.get(block).defaultModelState();

			if (result != null) {
				final int species = species(stack);

				if (species != AUTO_SELECT_SPECIES) {
					result.species(species);
				}

				result.primitiveBits(block instanceof PipeBlock && ((PipeBlock) block).hasGlow ? BasePipeModel.GLOW_BIT : 0);
			}
		}

		return result;
	};
}
