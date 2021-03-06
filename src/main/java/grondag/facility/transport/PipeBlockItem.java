package grondag.facility.transport;

import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import grondag.facility.transport.model.BasePipeModel;
import grondag.xm.api.block.XmBlockState;
import grondag.xm.api.modelstate.MutableModelState;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;


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

			final int spec = species(itemStack);
			final Text msg = spec == AUTO_SELECT_SPECIES ? new TranslatableText("transport.facility.circuit.auto") : new TranslatableText("transport.facility.circuit.num", spec);
			playerEntity.sendMessage(msg, true);
			return TypedActionResult.success(itemStack);
		}
	}

	private static NbtCompound cycleTag(ItemStack itemStack, boolean reverse) {
		NbtCompound tag = itemStack.getTag();

		if (tag == null) {
			tag = new NbtCompound();
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
		final NbtCompound tag = itemStack.getTag();

		if (tag == null || !tag.contains(SPECIES)) {
			return AUTO_SELECT_SPECIES;
		} else {
			return tag.getInt(SPECIES);
		}
	}

	public static final BiFunction<ItemStack, World, MutableModelState> PIPE_ITEM_MODEL_FUNCTION  = (stack, world) -> {
		MutablePrimitiveState result = null;

		if (stack.getItem() instanceof PipeBlockItem) {
			final Block block = ((BlockItem) stack.getItem()).getBlock();

			result = XmBlockState.get(block).defaultModelState();

			if (result != null) {
				final int species = species(stack);

				if (species != AUTO_SELECT_SPECIES) {
					result.species(species);
				}

				result.primitiveBits(block instanceof PipeBlock && ((PipeBlock) block).hasGlow ? BasePipeModel.GLOW_BIT : 0);
			}
		}

		return result;
	};
}
