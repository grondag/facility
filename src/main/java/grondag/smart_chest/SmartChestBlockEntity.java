package grondag.smart_chest;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.entity.BlockEntity;

public class SmartChestBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {
    
    public SmartChestBlockEntity() {
        super(SmartChest.SMART_CHEST_BLOCK_ENTITY_TYPE);
    }
    
    @Override
    public Object getRenderAttachmentData() {
        return this;
    }
}
