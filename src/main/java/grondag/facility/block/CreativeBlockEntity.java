/*******************************************************************************
 * Copyright 2019, 2020 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.facility.block;

import java.util.Random;
import java.util.Set;

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

import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.storage.ArticleConsumer;
import grondag.fluidity.api.storage.ArticleSupplier;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;

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
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.get(be).applyIfPresent(neighborSide, p ->
		p.attachIfPresent(ArticleType.ITEM, this, () -> ArticleConsumer.FULL, () -> ArticleSupplier.CREATIVE));
	}
}
