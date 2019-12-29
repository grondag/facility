package grondag.facility.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class AbstractFunctionalBlock extends Block implements BlockEntityProvider {

	protected final Supplier<BlockEntity> beFactory;

	public AbstractFunctionalBlock(Settings settings, Supplier<BlockEntity> beFactory) {
		super(settings);
		this.beFactory = beFactory;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return beFactory.get();
	}

	@Override
	public boolean hasBlockEntity() {
		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		updateBe(iWorld, blockPos);
		return blockState;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		updateBe(world, blockPos);
		super.onBlockAdded(blockState, world, blockPos, blockState2, bl);
	}

	@SuppressWarnings("rawtypes")
	protected void updateBe(IWorld world, BlockPos pos) {
		final BlockEntity be = world.getBlockEntity(pos);

		if(be instanceof PipeBlockEntity) {
			((AbstractFunctionalBlockEntity) be).updateNeighbors();
		}
	}

}