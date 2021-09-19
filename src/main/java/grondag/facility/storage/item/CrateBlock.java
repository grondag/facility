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
package grondag.facility.storage.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
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
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import grondag.facility.init.ScreenHandlers;
import grondag.facility.storage.PortableStore;
import grondag.facility.storage.StorageBlock;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.article.StoredDiscreteArticle;
import grondag.fluidity.base.storage.discrete.AbstractDiscreteStore;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.world.BlockTest;

public class CrateBlock extends StorageBlock {
	// ugly but works
	public PortableCrateItem portableItem;

	public CrateBlock(Block.Properties settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory) {
		super(settings, beFactory);
	}

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST = ctx -> canConnect(ctx.fromBlockState(), ctx.toBlockState());

	public static boolean canConnect(BlockState fromState, BlockState toState) {
		return fromState.getBlock() instanceof CrateBlock
		&& toState.getBlock() instanceof CrateBlock
		&& fromState.getValue(SpeciesProperty.SPECIES) == toState.getValue(SpeciesProperty.SPECIES);
	}

	public static boolean canConnect(CrateBlockEntity fromEntity, CrateBlockEntity toEntity) {
		final Level fromWorld = fromEntity.getLevel();
		return fromWorld == null || fromWorld != toEntity.getLevel() ? false : canConnect(fromEntity.getBlockState(), toEntity.getBlockState());
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(Block.byItem(player.getItemInHand(hand).getItem()) instanceof CrateBlock) {
			return InteractionResult.PASS;
		}

		if (!world.isClientSide) {
			final BlockEntity be = world.getBlockEntity(pos);

			if(be instanceof CrateBlockEntity) {
				final String label = ((CrateBlockEntity) be).getLabel();
				((ServerPlayer) player).openMenu(ScreenHandlers.crateBlockFactory(label, pos));
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockView, List<Component> list, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, blockView, list, tooltipContext);
		final CompoundTag beTag = itemStack.getTagElement("BlockEntityTag");

		if (beTag != null && beTag.contains(CrateBlockEntity.TAG_STORAGE)) {
			final ListTag tagList = beTag.getCompound(CrateBlockEntity.TAG_STORAGE).getList(AbstractDiscreteStore.TAG_ITEMS, 10);
			final int limit = Math.min(9,tagList.size());
			final StoredDiscreteArticle lookup = new StoredDiscreteArticle();

			for(int i = 0; i < limit; i++) {
				lookup.readTag(tagList.getCompound(i));

				if(!lookup.isEmpty()) {
					final MutableComponent text = lookup.article().toStack().getHoverName().plainCopy();
					text.append(" x").append(String.valueOf(lookup.count()));
					list.add(text);
				}
			}

			if(limit < tagList.size()) {
				list.add(new TextComponent("..."));
			}
		}
	}

	@Override
	protected ItemStack getStack(boolean isEmpty) {
		return isEmpty || portableItem == null ? new ItemStack(this) : new ItemStack(portableItem);
	}

	@Override
	protected void writeCustomStackData(ItemStack stack, Store store) {
		if(stack.getItem() == portableItem) {
			PortableStore.writeDamage(stack, store);
		}
	}
}
