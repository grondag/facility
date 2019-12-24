package grondag.contained.block;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import grondag.fluidity.api.storage.DiscreteStorage;
import grondag.xm.api.block.XmProperties;

public class BinStorageBlock extends ItemStorageBlock {
	public BinStorageBlock(Supplier<BlockEntity> beFactory) {
		super(beFactory);
	}


	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && state.getBlock() == this && hit.getSide() == state.get(XmProperties.FACE).getOpposite()) {
			final BlockEntity be = world.getBlockEntity(pos);

			if(be instanceof ItemStorageBlockEntity) {
				if(player != null) {
					final ItemStack stack =  player.getStackInHand(hand);

					if(stack != null && !stack.isEmpty()) {
						final DiscreteStorage storage = ((ItemStorageBlockEntity)be).getDiscreteStorage();

						final int q = (int) storage.accept(stack, false);

						if(q != 0) {
							stack.decrement(q);
							player.setStackInHand(hand, stack.isEmpty() ? ItemStack.EMPTY : stack);
							player.inventory.markDirty();
							return ActionResult.SUCCESS;
						}
					}
				}
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}


	@Override
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		if (world.isClient) {
			//TODO: remove
			System.out.println("boop");
		}
	}
}
