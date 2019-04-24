package grondag.smart_chest;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class SmartChestBlock extends Block implements BlockEntityProvider {
    public SmartChestBlock() {
        super(FabricBlockSettings.of(Material.STONE).dynamicBounds().strength(1, 1).build());
    }
    
    @Override
    public boolean hasBlockEntity() {
        return true;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new SmartChestBlockEntity();
    }
}
