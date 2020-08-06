package grondag.facility.transport;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class PipeBlockItem extends BlockItem {
	public final int species;
	public final boolean glow;

	public PipeBlockItem(Block block, Settings settings, int species, boolean glow) {
		super(block, settings);
		this.species = species;
		this.glow = glow;
	}
}
