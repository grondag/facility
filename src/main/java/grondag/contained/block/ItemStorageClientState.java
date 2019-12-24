package grondag.contained.block;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import grondag.contained.ContainedConfig;

public class ItemStorageClientState {
	public final ItemStorageBlockEntity owner;
	public @Nullable ItemStack[] renderStacks = null;

	/**
	 * on client, caches last result from
	 */
	private long lastDistanceSquared;

	private float displayAlpha = 1f;

	public ItemStorageClientState(ItemStorageBlockEntity owner) {
		this.owner = owner;
	}

	public void updateLastDistanceSquared(double dSq) {
		final long d = (long) dSq;
		final long lastD = lastDistanceSquared;

		if(d != lastD) {
			// fade effect isn't great, so disable for now
			//			// fade in controls as player approaches - over a 4-block distance
			//			float a  =  1f - ((float) Math.sqrt(d) - 8) * 0.25f; //ContainedConfig.maxRenderDistance) * 0.25f;
			//
			//			if (a < 0) {
			//				a = 0;
			//			} else if (a > 1) {
			//				a = 1;
			//			}

			displayAlpha = d > ContainedConfig.maxRenderDistanceSq ? 0 : 1;
			lastDistanceSquared = d;
		}
	}

	public float displayAlpha() {
		return displayAlpha;
	}

	public String label() {
		return owner.label;
	}
}
