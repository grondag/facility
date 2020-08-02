package grondag.facility.storage;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class TrackedBlockEntity extends BlockEntity {
	public TrackedBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	public abstract void onLoaded();

	public abstract void onUnloaded();
}
