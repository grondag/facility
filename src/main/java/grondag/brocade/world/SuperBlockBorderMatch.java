package grondag.brocade.world;

import java.util.function.BiPredicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class SuperBlockBorderMatch<V> extends AbstractNonFaceTest<V>
{
    private final Block block;
    private final V matchModelState;
    private final ModelStateFunction<V> stateFunc;
    private final BiPredicate<V, V> matchFunc;
    
    /** pass in the info for the block you want to match */
    public SuperBlockBorderMatch(Block block, V modelState, ModelStateFunction<V> stateFunc, BiPredicate<V, V> matchFunc) {
        this.block = block;
        this.matchModelState = modelState;
        this.stateFunc = stateFunc;
        this.matchFunc = matchFunc;
    }
    
    /** assumes you want to match block at given position */
    public SuperBlockBorderMatch(BlockView world, BlockState blockState, BlockPos pos, ModelStateFunction<V> stateFunc, BiPredicate<V, V> matchFunc) {
        this.block = blockState.getBlock();
        //last param = false prevents recursion - we don't need the full model state (which depends on this logic)
        this.matchModelState = stateFunc.get(world, pos, blockState);
        this.stateFunc = stateFunc;
        this.matchFunc = matchFunc;
    }
    
    @Override 
    public boolean wantsModelState() { return true; }
    
    @Override
    protected boolean testBlock(BlockView world, BlockState blockState, BlockPos pos, V modelState) {
        return blockState.getBlock() == this.block && matchFunc.test(modelState, this.matchModelState);
    }

    @Override
    protected boolean testBlock(BlockView world, BlockState blockState, BlockPos pos) {
        return blockState.getBlock() == this.block && matchFunc.test(stateFunc.get(world, pos, blockState), this.matchModelState);
    }
    
}
