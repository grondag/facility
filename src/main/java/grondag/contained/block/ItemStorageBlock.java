package grondag.contained.block;

import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

public class ItemStorageBlock extends AbstractStorageBlock {

	public ItemStorageBlock() {
		super(FabricBlockSettings.of(Material.STONE).dynamicBounds().strength(1, 1).build());
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new ItemStorageBlockEntity();
	}

}
