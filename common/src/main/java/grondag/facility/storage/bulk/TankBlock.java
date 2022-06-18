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

package grondag.facility.storage.bulk;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.BlockAttackInteractionAware;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import grondag.facility.storage.PortableStore;
import grondag.facility.storage.StorageBlock;
import grondag.facility.storage.item.CrateBlockEntity;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.article.StoredDiscreteArticle;
import grondag.fluidity.base.storage.discrete.AbstractDiscreteStore;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.world.BlockTest;

public class TankBlock extends StorageBlock implements BlockAttackInteractionAware {
	public final boolean isCreative;

	public TankBlock(Block.Properties settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory, boolean isCreative) {
		super(settings, beFactory);
		this.isCreative = isCreative;
	}

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST = ctx -> canConnect(ctx.fromBlockState(), ctx.toBlockState());

	public static boolean canConnect(BlockState fromState, BlockState toState) {
		return fromState.getBlock() instanceof TankBlock
			&& toState.getBlock() instanceof TankBlock
			&& fromState.getValue(SpeciesProperty.SPECIES) == toState.getValue(SpeciesProperty.SPECIES);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		final ItemStack stack = player.getItemInHand(hand);

		if (Block.byItem(stack.getItem()) instanceof TankBlock) {
			return InteractionResult.PASS;
		}

		if (!world.isClientSide) {
			final BlockEntity be = world.getBlockEntity(pos);

			if (be instanceof final TankBlockEntity tankBe) {
				if (Store.STORAGE_COMPONENT.applyActionsWithHeld(tankBe.getEffectiveStorage(), (ServerPlayer) player)) {
					return InteractionResult.SUCCESS;
				} //else {
				//					final String label = tankBe.getLabel();
				//
				//					ContainerProviderRegistry.INSTANCE.openContainer(TankContainer.ID, player, p -> {
				//						p.writeBlockPos(pos);
				//						p.writeString(label);
				//					});
				//				}
			}
		}

		return InteractionResult.SUCCESS;
	}

	protected static long lastClickMs = 0;

	@Override
	public boolean onAttackInteraction(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, Direction face) {
		//TODO: implement
		//		if(world.isClient && state.getBlock() == this) {
		//			final long t = System.currentTimeMillis();
		//			final long d = t - lastClickMs;
		//			lastClickMs = t;
		//
		//			if(d > 100 && face.getOpposite() == state.get(XmProperties.FACE)) {
		//				TankActionC2S.send(pos, getHitHandle(world, player, face), true);
		//			}
		//		}
		//
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockView, List<Component> list, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, blockView, list, tooltipContext);
		final CompoundTag beTag = itemStack.getTagElement("BlockEntityTag");

		if (beTag != null && beTag.contains(CrateBlockEntity.TAG_STORAGE)) {
			final ListTag tagList = beTag.getCompound(CrateBlockEntity.TAG_STORAGE).getList(AbstractDiscreteStore.TAG_ITEMS, 10);
			final int limit = Math.min(9, tagList.size());
			final StoredDiscreteArticle lookup = new StoredDiscreteArticle();

			for (int i = 0; i < limit; i++) {
				lookup.readTag(tagList.getCompound(i));

				if (!lookup.isEmpty()) {
					final MutableComponent text = lookup.article().toStack().getHoverName().plainCopy();
					text.append(" x").append(String.valueOf(lookup.count()));
					list.add(text);
				}
			}

			if (limit < tagList.size()) {
				list.add(Component.literal("..."));
			}
		}
	}

	@Override
	protected void writeCustomStackData(ItemStack stack, Store store) {
		PortableStore.writeDamage(stack, store);
	}
}
