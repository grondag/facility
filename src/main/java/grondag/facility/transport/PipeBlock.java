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
package grondag.facility.transport;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import grondag.facility.block.FacilitySpeciesBlock;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.xm.api.block.XmBlockState;
import grondag.xm.api.collision.CollisionDispatcher;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.world.BlockTest;

public class PipeBlock extends FacilitySpeciesBlock {
	public PipeBlock(Block.Settings settings, Supplier<BlockEntity> beFactory) {
		super(settings, beFactory);
	}

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST = ctx -> canConnect(ctx.fromBlockEntity(), ctx.toBlockEntity());

	public static boolean canConnect(BlockEntity fromEntity, BlockEntity toEntity) {
		if(fromEntity == null || toEntity == null) {
			return false;
		}

		final World fromWorld = fromEntity.getWorld();

		if(fromWorld == null || fromWorld != toEntity.getWorld()) {
			return false;
		}

		final boolean fromPipe = fromEntity instanceof PipeBlockEntity;
		final boolean toPipe = toEntity instanceof PipeBlockEntity;

		if(fromPipe) {
			if(toPipe) {
				return fromEntity.getCachedState().get(SpeciesProperty.SPECIES) == toEntity.getCachedState().get(SpeciesProperty.SPECIES);
			} else {
				return toEntity instanceof CarrierConnector;
			}
		} else if(toPipe) {
			return fromEntity instanceof CarrierConnector;
		} else {
			return false;
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos pos, EntityContext entityContext) {
		return CollisionDispatcher.shapeFor(XmBlockState.modelState(blockState, blockView, pos, true));
	}
}
