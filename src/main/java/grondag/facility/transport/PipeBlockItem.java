package grondag.facility.transport;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;


public class PipeBlockItem extends BlockItem {
	public static final int AUTO_SELECT_SPECIES = -1;

	private static final String SPECIES = "species";

	public PipeBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		if (world.isClient) {
			return super.use(world, playerEntity, hand);
		} else {
			final ItemStack itemStack = playerEntity.getStackInHand(hand);
			itemStack.setTag(cycleTag(itemStack, playerEntity.isSneaking()));
			playerEntity.setStackInHand(hand, itemStack);
			return TypedActionResult.success(itemStack);
		}
	}

	private static CompoundTag cycleTag(ItemStack itemStack, boolean reverse) {
		CompoundTag tag = itemStack.getTag();

		if (tag == null) {
			tag = new CompoundTag();
			tag.putInt(SPECIES, 0);
		} else if (tag.contains(SPECIES)) {
			// has species, so increment
			final int species = tag.getInt(SPECIES) + (reverse ? -1 : 1);

			// reset to auto on loop
			if (species > 15 || species < 0) {
				tag.remove(SPECIES);

				if (tag.isEmpty()) {
					tag = null;
				}
			} else {
				tag.putInt(SPECIES, species);
			}
		} else {
			// has tag (wierd) but no species, so start cycle
			tag.putInt(SPECIES, 0);
		}

		return tag;
	}

	public static int species(ItemStack itemStack) {
		final CompoundTag tag = itemStack.getTag();

		if (tag == null || !tag.contains(SPECIES)) {
			return AUTO_SELECT_SPECIES;
		} else {
			return tag.getInt(SPECIES);
		}
	}
}
