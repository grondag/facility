package grondag.smart_chest;

import net.minecraft.block.entity.BlockEntity;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

public class SmartChestBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {
	public SmartChestBlockEntity() {
		super(Registrations.SMART_CHEST_BLOCK_ENTITY_TYPE);
	}

	@Override
	public Object getRenderAttachmentData() {
		return this;
	}
}
