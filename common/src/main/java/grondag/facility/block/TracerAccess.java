package grondag.facility.block;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class TracerAccess extends Item {
	public static HitResult trace(Level world, Player player) {
		return getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
	}

	protected TracerAccess() {
		super(new Properties());
	}
}