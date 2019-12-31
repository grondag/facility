package grondag.facility.block;

import java.util.Random;
import java.util.Set;

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

import grondag.facility.wip.transport.CarrierProvider;
import grondag.facility.wip.transport.CarrierSession;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.Authorization;
import grondag.fluidity.api.device.ComponentType;
import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;
import grondag.fluidity.api.storage.Storage;

public class CreativeBlockEntity extends CarrierSessionBlockEntity implements Tickable {
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
				final CarrierSession s = neighbors.get(i);
				s.broadcastConsumer().accept(stack, false);
			}
		}
	}

	@Override
	public Set<ArticleType<?>> articleTypes() {
		return ArticleType.SET_OF_ITEMS;
	}

	@Override
	protected CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.applyIfPresent(be, neighborSide, p ->
		p.attachIfPresent(ArticleType.ITEM, this, () -> ArticleConsumer.FULL, () -> ArticleSupplier.CREATIVE));
	}

	@Override
	protected <T> T getOtherComponent(ComponentType<T> serviceType, Authorization auth, Direction side, Identifier id) {
		if(serviceType == Storage.STORAGE_COMPONENT || serviceType == Storage.INTERNAL_STORAGE_COMPONENT) {
			return serviceType.cast(Storage.CREATIVE);
		} else {
			return serviceType.absent();
		}
	}
}
