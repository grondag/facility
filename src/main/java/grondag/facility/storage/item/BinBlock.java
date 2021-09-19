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

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.fabric.api.block.BlockAttackInteractionAware;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import grondag.facility.block.TracerAccess;
import grondag.facility.packet.BinActionC2S;
import grondag.fermion.orientation.api.FaceCorner;
import grondag.fermion.orientation.api.FaceEdge;
import grondag.fermion.world.WorldHelper;
import grondag.xm.api.block.XmProperties;

public class BinBlock extends CrateBlock implements BlockAttackInteractionAware {
	public final int divisionLevel;
	public final boolean isCreative;

	public BinBlock(Block.Properties settings, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> beFactory, int divisionLevel, boolean isCreative) {
		super(settings, beFactory);
		this.divisionLevel = divisionLevel;
		this.isCreative = isCreative;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (state.getBlock() == this && hit.getDirection() == state.getValue(XmProperties.FACE).getOpposite() && hand == InteractionHand.MAIN_HAND) {
			if(world.isClientSide) {
				BinActionC2S.send(pos, getHitHandle(hit, hit.getDirection()), false);
			}

			return InteractionResult.SUCCESS;
		}

		return super.use(state, world, pos, player, hand, hit);
	}


	protected static long lastClickMs = 0;

	@Override
	public boolean onAttackInteraction(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, Direction face) {
		if(world.isClientSide && state.getBlock() == this) {
			final long t = System.currentTimeMillis();
			final long d = t - lastClickMs;
			lastClickMs = t;

			if(d > 100 && face.getOpposite() == state.getValue(XmProperties.FACE)) {
				BinActionC2S.send(pos, getHitHandle(world, player, face), true);
			}
		}

		return false;
	}

	protected int getHitHandle(Level world, Player player, Direction face) {
		return divisionLevel > 1 ? getHitHandle(TracerAccess.trace(world, player), face) : 0;
	}

	protected int getHitHandle(HitResult hit, Direction face) {
		if(divisionLevel > 1) {
			if(hit != null && hit.getType() == HitResult.Type.BLOCK)  {
				final Vec3 vec = hit.getLocation();
				final Pair<Direction, Direction> faces = WorldHelper.closestAdjacentFaces(face, vec.x, vec.y, vec.z);
				final FaceEdge e1 = FaceEdge.fromWorld(faces.getLeft(), face);
				final FaceEdge e2 = FaceEdge.fromWorld(faces.getRight(), face);
				final FaceCorner corner = FaceCorner.find(e1, e2);

				switch(corner) {
				case TOP_LEFT:
				default:
					return 0;
				case TOP_RIGHT:
					return divisionLevel == 2 ? 0 : 1;
				case BOTTOM_LEFT:
					return divisionLevel == 2 ? 1 : 2;
				case BOTTOM_RIGHT:
					return divisionLevel == 2 ? 1 : 3;
				}
			}
		}

		return 0;
	}
}
