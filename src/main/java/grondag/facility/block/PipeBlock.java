package grondag.facility.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;

public class PipeBlock extends AbstractFunctionalBlock {
	public PipeBlock(Block.Settings settings, Supplier<BlockEntity> beFactory) {
		super(settings, beFactory);
	}
}
