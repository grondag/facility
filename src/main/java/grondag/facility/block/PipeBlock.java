package grondag.facility.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import grondag.facility.wip.transport.NodeDevice;
import grondag.xm.api.block.XmBlockState;
import grondag.xm.api.collision.CollisionDispatcher;
import grondag.xm.api.connect.species.SpeciesFunction;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.world.BlockTest;

public class PipeBlock extends AbstractFunctionalBlock {
	public final SpeciesFunction speciesFunc = SpeciesProperty.speciesForBlock(this);

	public PipeBlock(Block.Settings settings, Supplier<BlockEntity> beFactory) {
		super(settings, beFactory);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(SpeciesProperty.SPECIES);
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
				return toEntity instanceof NodeDevice;
			}
		} else if(toPipe) {
			return fromEntity instanceof NodeDevice;
		} else {
			return false;
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos pos, EntityContext entityContext) {
		return CollisionDispatcher.shapeFor(XmBlockState.modelState(blockState, blockView, pos, true));
	}
}
