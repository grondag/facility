package grondag.brocade.world;



import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@FunctionalInterface
public interface ExtraStateFactory {
    public static <T> T NONE(BlockView worldIn, BlockPos pos, BlockState state) {
        return null;
    }
    
    public <V> V get(BlockView worldIn, BlockPos pos, BlockState state);
}
