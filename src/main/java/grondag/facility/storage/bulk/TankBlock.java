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
package grondag.facility.storage.bulk;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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

	public TankBlock(Block.Settings settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory, boolean isCreative) {
		super(settings, beFactory);
		this.isCreative = isCreative;
	}

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST = ctx -> canConnect(ctx.fromBlockState(), ctx.toBlockState());

	public static boolean canConnect(BlockState fromState, BlockState toState) {
		return fromState.getBlock() instanceof TankBlock
		&& toState.getBlock() instanceof TankBlock
		&& fromState.get(SpeciesProperty.SPECIES) == toState.get(SpeciesProperty.SPECIES);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		final ItemStack stack = player.getStackInHand(hand);

		if(Block.getBlockFromItem(stack.getItem()) instanceof TankBlock) {
			return ActionResult.PASS;
		}

		if (!world.isClient) {
			final BlockEntity be = world.getBlockEntity(pos);

			if(be instanceof TankBlockEntity) {
				final TankBlockEntity tankBe = (TankBlockEntity) be;

				if(Store.STORAGE_COMPONENT.applyActionsWithHeld(tankBe.getEffectiveStorage(), (ServerPlayerEntity)player)) {
					return ActionResult.SUCCESS;
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

		return ActionResult.SUCCESS;
	}

	protected static long lastClickMs = 0;

	@Override
	public boolean onAttackInteraction(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction face) {
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
	public void appendTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
		super.appendTooltip(itemStack, blockView, list, tooltipContext);
		final NbtCompound beTag = itemStack.getSubTag("BlockEntityTag");

		if (beTag != null && beTag.contains(CrateBlockEntity.TAG_STORAGE)) {
			final NbtList tagList = beTag.getCompound(CrateBlockEntity.TAG_STORAGE).getList(AbstractDiscreteStore.TAG_ITEMS, 10);
			final int limit = Math.min(9,tagList.size());
			final StoredDiscreteArticle lookup = new StoredDiscreteArticle();

			for(int i = 0; i < limit; i++) {
				lookup.readTag(tagList.getCompound(i));

				if(!lookup.isEmpty()) {
					final MutableText text = lookup.article().toStack().getName().copy();
					text.append(" x").append(String.valueOf(lookup.count()));
					list.add(text);
				}
			}

			if(limit < tagList.size()) {
				list.add(new LiteralText("..."));
			}
		}
	}

	@Override
	protected void writeCustomStackData(ItemStack stack, Store store) {
		PortableStore.writeDamage(stack, store);
	}
}
