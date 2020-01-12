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
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import grondag.facility.storage.StorageBlock;
import grondag.fluidity.base.article.StoredDiscreteArticle;
import grondag.fluidity.base.storage.discrete.AbstractDiscreteStorage;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.world.BlockTest;

public class CrateBlock extends StorageBlock {
	public CrateBlock(Block.Settings settings, Supplier<BlockEntity> beFactory) {
		super(settings, beFactory);
	}

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST = ctx -> canConnect(ctx.fromBlockState(), ctx.toBlockState());

	public static boolean canConnect(BlockState fromState, BlockState toState) {
		return fromState.getBlock() instanceof CrateBlock
				&& toState.getBlock() instanceof CrateBlock
				&& fromState.get(SpeciesProperty.SPECIES) == toState.get(SpeciesProperty.SPECIES);
	}

	public static boolean canConnect(CrateBlockEntity fromEntity, CrateBlockEntity toEntity) {
		final World fromWorld = fromEntity.getWorld();
		return fromWorld == null || fromWorld != toEntity.getWorld() ? false : canConnect(fromEntity.getCachedState(), toEntity.getCachedState());
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(Block.getBlockFromItem(player.getStackInHand(hand).getItem()) instanceof CrateBlock) {
			return ActionResult.PASS;
		}

		if (!world.isClient) {
			final BlockEntity be = world.getBlockEntity(pos);

			if(be instanceof CrateBlockEntity) {
				final String label = ((CrateBlockEntity) be).getLabel();

				ContainerProviderRegistry.INSTANCE.openContainer(CrateContainer.ID, player, p -> {
					p.writeBlockPos(pos);
					p.writeString(label);
				});
			}
		}

		return ActionResult.SUCCESS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void buildTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
		super.buildTooltip(itemStack, blockView, list, tooltipContext);
		final CompoundTag beTag = itemStack.getSubTag("BlockEntityTag");

		if (beTag != null && beTag.contains(CrateBlockEntity.TAG_STORAGE)) {
			final ListTag tagList = beTag.getCompound(CrateBlockEntity.TAG_STORAGE).getList(AbstractDiscreteStorage.TAG_ITEMS, 10);
			final int limit = Math.min(9,tagList.size());
			final StoredDiscreteArticle lookup = new StoredDiscreteArticle();

			for(int i = 0; i < limit; i++) {
				lookup.readTag(tagList.getCompound(i));

				if(!lookup.isEmpty()) {
					final Text text = lookup.article().toStack().getName().deepCopy();
					text.append(" x").append(String.valueOf(lookup.count()));
					list.add(text);
				}
			}

			if(limit < tagList.size()) {
				list.add(new LiteralText("..."));
			}
		}
	}
}
