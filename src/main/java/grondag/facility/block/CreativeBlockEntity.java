package grondag.facility.block;

import java.util.Random;

import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import grondag.facility.wip.transport.CarrierDevice;
import grondag.facility.wip.transport.NodeDevice;
import grondag.fluidity.api.device.Device;
import grondag.fluidity.api.storage.Storage;

public class CreativeBlockEntity extends AbstractFunctionalBlockEntity<Storage> implements Tickable, NodeDevice {
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

		if(neighbors.isEmpty()) {
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
				final Storage s = neighbors.get(i);
				s.getConsumer().accept(stack, false);
			}
		}
	}

	@Override
	protected void addNeighbor(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		if(be instanceof Device && ((Device) be).hasStorage(neighborSide)) {
			neighbors.add(((Device) be).getStorage(neighborSide));
		}
	}

	@Override
	public Storage getStorage(Direction side, Identifier id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCarrierPresent(CarrierDevice carrierDevice) {
		// TODO Auto-generated method stub

	}
}
