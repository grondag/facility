package grondag.smart_chest;

import grondag.frex.api.model.DynamicModelBlockEntity;
import net.minecraft.block.entity.BlockEntity;

public class SmartChestBlockEntity extends BlockEntity implements DynamicModelBlockEntity {
    
    public SmartChestBlockEntity() {
        super(SmartChest.SMART_CHEST_BLOCK_ENTITY_TYPE);
    }
    
    @Override
    public Object getDynamicModelData() {
        return this;
    }
}
