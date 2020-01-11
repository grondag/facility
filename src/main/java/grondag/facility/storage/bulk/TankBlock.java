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

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.block.BlockAttackInteractionAware;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import grondag.facility.block.FacilitySpeciesBlock;

public class TankBlock extends FacilitySpeciesBlock implements BlockAttackInteractionAware {
	public final boolean isCreative;

	public TankBlock(Block.Settings settings, Supplier<BlockEntity> beFactory, boolean isCreative) {
		super(settings, beFactory);
		this.isCreative = isCreative;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(Block.getBlockFromItem(player.getStackInHand(hand).getItem()) instanceof TankBlock) {
			return ActionResult.PASS;
		}

		if (!world.isClient) {
			final BlockEntity be = world.getBlockEntity(pos);

			if(be instanceof TankBlockEntity) {
				final String label = ((TankBlockEntity) be).getLabel();

				ContainerProviderRegistry.INSTANCE.openContainer(TankContainer.ID, player, p -> {
					p.writeBlockPos(pos);
					p.writeString(label);
				});
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
}
