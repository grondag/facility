package grondag.facility.block;

import java.util.Random;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import grondag.facility.wip.transport.CarrierDevice;
import grondag.facility.wip.transport.CarrierNode;
import grondag.facility.wip.transport.NodeDevice;
import grondag.fluidity.api.device.StorageProvider;

public class CreativeBlockEntity extends AbstractFunctionalBlockEntity<CarrierNode> implements Tickable, NodeDevice {
	protected final boolean isOutput;
	protected boolean isFirstTick = true;

	public CreativeBlockEntity(BlockEntityType<CreativeBlockEntity> type, boolean isOutput) {
		super(type);
		this.isOutput = isOutput;
	}

	@Override
	public void tick() {
		if(world.isClient) {
			return;
		}

		if(isFirstTick) {
			updateNeighbors();
			isFirstTick = false;
		}

		if(neighbors.isEmpty() || !isReceivingRedstonePower()) {
			return;
		}

		final int limit = neighbors.size();
		final Random random =  ThreadLocalRandom.current();
		final Item item = Registry.ITEM.getRandom(random);
		ItemStack stack = new ItemStack(item);
		stack.setCount(item.getMaxCount());

		if(item.isDamageable()) {
			stack.setDamage(random.nextInt(item.getMaxDamage()));
		}

		if(stack.isEnchantable() && random.nextBoolean()) {
			stack = EnchantmentHelper.enchant(random, stack, 30, true);
		}

		if(isOutput) {
			for(int i = 0; i < limit; i++) {
				final CarrierNode s = neighbors.get(i);
				s.broadcastConsumer().accept(stack, false);
			}
		}
	}

	@Override
	protected void addNeighbor(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		if(be instanceof CarrierDevice) {
			final CarrierNode n = ((CarrierDevice) be).attach(this, neighborSide);
			if(n != null && n.isValid()) {
				neighbors.add(n);
			}
		}
	}

	@Override
	public StorageProvider getStorageProvider() {
		return StorageProvider.CREATIVE;
	}

	@Override
	public void onCarrierPresent(CarrierDevice carrierDevice) {
		// TODO Auto-generated method stub

	}

	protected static final BlockPos.Mutable searchPos = new BlockPos.Mutable();

	protected boolean isReceivingRedstonePower() {
		final BlockPos pos = this.pos;
		final World world = this.world;

		if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.DOWN), Direction.UP) > 0) {
			return true;
		} else if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.UP), Direction.DOWN) > 0) {
			return true;
		} else if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.NORTH), Direction.SOUTH) > 0) {
			return true;
		} else if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.SOUTH), Direction.NORTH) > 0) {
			return true;
		} else if (world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.WEST), Direction.EAST) > 0) {
			return true;
		} else {
			return world.getEmittedRedstonePower(searchPos.set(pos).setOffset(Direction.EAST), Direction.WEST) > 0;
		}
	}
}
