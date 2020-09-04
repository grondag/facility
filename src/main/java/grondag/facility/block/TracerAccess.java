package grondag.facility.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class TracerAccess extends Item {
	public static HitResult trace(World world, PlayerEntity player) {
		return raycast(world, player, RaycastContext.FluidHandling.NONE);
	}

	protected TracerAccess() {
		super(new Settings());
	}
}