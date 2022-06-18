/*
 * This file is part of Facility and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.facility.storage.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import grondag.facility.block.CarrierSessionBlockEntity;
import grondag.facility.storage.TickableBlockEntity;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;

public class CreativeCrateBlockEntity extends CarrierSessionBlockEntity implements TickableBlockEntity {
	protected final boolean isOutput;
	private final RandomSource random = RandomSource.create();

	public CreativeCrateBlockEntity(BlockEntityType<CreativeCrateBlockEntity> type, final BlockPos pos, final BlockState state, final boolean isOutput) {
		super(type, pos, state);
		this.isOutput = isOutput;
	}

	@Override
	public void tick() {
		final int limit = neighborCount();

		if (limit == 0 || !getBlockState().getValue(BlockStateProperties.POWERED)) {
			return;
		}

		final Item item = Registry.ITEM.getRandom(random).get().value();
		ItemStack stack = new ItemStack(item);
		stack.setCount(item.getMaxStackSize());

		if (item.canBeDepleted() && item.getMaxDamage() > 0) {
			stack.setDamageValue(random.nextInt(item.getMaxDamage()));
		}

		if (stack.isEnchantable() && random.nextBoolean()) {
			stack = EnchantmentHelper.enchantItem(random, stack, 30, true);
		}

		if (isOutput) {
			for (int i = 0; i < limit; i++) {
				final CarrierSession s = getNeighbor(i);
				s.broadcastConsumer().apply(stack, false);
			}
		}
	}

	@Override
	protected CarrierSession getSession(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
		return CarrierProvider.CARRIER_PROVIDER_COMPONENT.getAccess(be).applyIfPresent(neighborSide, p ->
		p.attachIfPresent(ArticleType.ITEM, ct -> ct.getAccess(this)));
	}

	@Override
	public void onLoaded() {
		// NOOP
	}

	@Override
	public void onUnloaded() {
		// NOOP
	}
}
