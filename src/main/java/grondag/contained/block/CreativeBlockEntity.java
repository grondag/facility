package grondag.contained.block;

import java.util.Random;

import io.netty.util.internal.ThreadLocalRandom;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.registry.Registry;

import grondag.fluidity.api.device.Device;
import grondag.fluidity.api.storage.Storage;

public class CreativeBlockEntity extends BlockEntity  implements Tickable {
	protected final boolean isOutput;
	protected boolean isFirstTick = true;

	protected final ObjectArrayList<Storage> neighbors = new ObjectArrayList<>();

	public CreativeBlockEntity(BlockEntityType<CreativeBlockEntity> type, boolean isOutput) {
		super(type);
		this.isOutput = isOutput;
	}

	public void updateNeighbors() {
		if(world == null || world.isClient) {
			return;
		}

		neighbors.clear();
		final long myPos = pos.asLong();

		try(BlockPos.PooledMutable p = BlockPos.PooledMutable.get()) {
			addNeighbor(p.set(BlockPos.add(myPos, -1, 0, 0)));
			addNeighbor(p.set(BlockPos.add(myPos, 1, 0, 0)));
			addNeighbor(p.set(BlockPos.add(myPos, 0, -1, 0)));
			addNeighbor(p.set(BlockPos.add(myPos, 0, 1, 0)));
			addNeighbor(p.set(BlockPos.add(myPos, 0, 0, -1)));
			addNeighbor(p.set(BlockPos.add(myPos, 0, 0, 1)));
		}
	}

	private void addNeighbor(Mutable searchPos) {
		final BlockEntity be = world.getBlockEntity(searchPos);

		if(be instanceof Device) {
			neighbors.add(((Device) be).getStorage());
		}
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
				s.accept(stack, false);
			}
		}
	}
}
