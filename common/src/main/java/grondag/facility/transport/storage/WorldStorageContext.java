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

package grondag.facility.transport.storage;

import java.util.Random;

import com.google.common.base.Predicates;
import io.netty.util.internal.ThreadLocalRandom;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import grondag.facility.FacilityConfig;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;

public abstract class WorldStorageContext implements TransportStorageContext {
	protected abstract Level world();

	protected static final int FLUID_COOLDOWN_TICKS = 64 / FacilityConfig.utb1ItemsPerTick;

	protected abstract BlockPos pos();

	protected Level world;
	protected BlockPos pos;
	protected BlockState blockState;
	protected Block block;

	Fluid fluid = null;
	ItemStack stack = null;
	ItemEntity entity = null;
	boolean canDropItems = false;
	boolean canPlaceFluid = false;
	AABB bounds = null;
	final ObjectArrayList<Entity> entityList = new ObjectArrayList<>();
	protected boolean drainable = false;
	protected boolean fillable = false;
	protected int fluidCooldownTicks = FLUID_COOLDOWN_TICKS;

	@Override
	public boolean prepareForTick() {
		fluid = null;
		stack = null;
		canDropItems = false;
		canPlaceFluid = false;
		drainable = false;
		fillable = false;

		final BlockPos oldPos = pos;
		pos = pos();
		world = world();

		if (pos == null || world == null) {
			return true;
		}

		if (pos != oldPos) {
			bounds = new AABB(pos);
		}

		final BlockState state = world.getBlockState(pos);
		blockState = state;
		final Block block = state.getBlock();
		this.block = block;

		if (--fluidCooldownTicks <= 0) {
			final FluidState fluidState = state.getFluidState();
			final Fluid worldFluid = fluidState.getType();
			fluid = null;

			if (worldFluid == Fluids.EMPTY) {
				if (state.isAir() && state.getMaterial().isReplaceable()) {
					canPlaceFluid = true;
				} else if (block instanceof LiquidBlockContainer) {
					canPlaceFluid = true;
					fillable = true;
				}
			} else if (worldFluid.isSource(fluidState)) {
				if (block instanceof LiquidBlock) {
					fluid = worldFluid;
					drainable = false;
				} else if (block instanceof BucketPickup) {
					fluid = worldFluid;
					drainable = true;
				}
			}
		}

		canDropItems = !state.isCollisionShapeFullBlock(world, pos);

		entityList.clear();
		// PERF: avoid letting vanilla allocate new list each time
		entityList.addAll(world.getEntitiesOfClass(ItemEntity.class, bounds, Predicates.alwaysTrue()));
		entity = entityList.isEmpty() ? null : (ItemEntity) entityList.get(0);
		stack = entity == null ? null : entity.getItem();

		return true;
	}

	@Override
	public boolean canAccept(Article article) {
		return (canPlaceFluid && article.isFluid()) || (canDropItems && article.isItem());
	}

	@Override
	public Article proposeSupply(ArticleType<?> type) {
		if (type.isFluid()) {
			return fluid == null ? Article.NOTHING : Article.of(fluid);
		} else if (type.isItem()) {
			return stack == null ? Article.NOTHING : Article.of(stack);
		} else {
			return Article.NOTHING;
		}
	}

	@Override
	public Article proposeAccept(ArticleType<?> type) {
		if (type.isFluid()) {
			if (canPlaceFluid) {
				return fillable ? Article.of(Fluids.WATER) : Article.NOTHING;
			} else {
				return null;
			}
		} else if (type.isItem()) {
			return canDropItems ? Article.NOTHING : null;
		}

		return null;
	}

	@Override
	public void advanceAcceptProposal(ArticleType<?> articleType) {
		// NOOP
	}

	@Override
	public long unitsFor(Article targetArticle) {
		return 1;
	}

	@Override
	public long capacityFor(Article article, long divisor) {
		if (divisor != 1) {
			return 0;
		}

		if (article.isFluid()) {
			if (canPlaceFluid) {
				if (fillable) {
					return ((LiquidBlockContainer) block).canPlaceLiquid(world, pos, blockState, article.toFluid()) ? 1 : 0;
				} else {
					return 1;
				}
			} else {
				return 0;
			}
		} else if (article.isItem()) {
			return canDropItems ? FacilityConfig.utb1ItemsPerTick : 0;
		} else {
			return 0;
		}
	}

	@Override
	public long accept(Article article, long numerator, long divisor) {
		if (divisor != 1 || numerator < 1) {
			return 0;
		}

		if (article.isFluid()) {
			if (canPlaceFluid && (!fillable || ((LiquidBlockContainer) block).canPlaceLiquid(world, pos, blockState, article.toFluid()))) {
				if (world.dimensionType().ultraWarm() && article.toFluid().is(FluidTags.WATER)) {
					final int i = pos.getX();
					final int j = pos.getY();
					final int k = pos.getZ();
					world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l) {
						world.addParticle(ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
					}

					fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
					return 1;
				} else {
					if (fillable) {
						if (((LiquidBlockContainer) block).placeLiquid(world, pos, blockState, article.toFluid().defaultFluidState())) {
							fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
							return 1;
						} else {
							return 0;
						}
					} else {
						world.setBlockAndUpdate(pos, article.toFluid().defaultFluidState().createLegacyBlock());
						fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
						return 1;
					}
				}
			} else {
				return 0;
			}
		} else if (article.isItem()) {
			if (canDropItems) {
				final long howMany = Math.min(numerator, FacilityConfig.utb1ItemsPerTick);
				int remaining = (int) howMany;

				if (!entityList.isEmpty()) {
					final int limit = entityList.size();

					for (int i = 0; i < limit; ++i) {
						final ItemEntity e = (ItemEntity) entityList.get(i);
						final ItemStack entityStack = e.getItem();

						if (article.matches(entityStack) && entityStack.getCount() < entityStack.getMaxStackSize()) {
							final int qty = Math.min(remaining, entityStack.getMaxStackSize() - entityStack.getCount());
							entityStack.grow(qty);
							e.setItem(entityStack);
							remaining -= qty;

							if (remaining == 0) {
								break;
							}
						}
					}
				}

				if (remaining > 0) {
					final ItemStack stack = article.toStack(howMany);
					final Random r = ThreadLocalRandom.current();

					final ItemEntity itemEntity = new ItemEntity(world,
							pos.getX() + 0.25 + 0.5 * r.nextDouble(),
							pos.getY() + 0.25 + 0.5 * r.nextDouble(),
							pos.getZ() + 0.25 + 0.5 * r.nextDouble(),
							stack);

					itemEntity.setDeltaMovement(0, 0, 0);
					world.addFreshEntity(itemEntity);
				}

				return howMany;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	@Override
	public boolean canSupply(Article article) {
		if (article.isNothing()) {
			return false;
		} else if (article.isFluid()) {
			return fluid != null && article.toFluid().equals(fluid);
		} else if (article.isItem()) {
			return stack != null && article.matches(stack);
		} else {
			return false;
		}
	}

	@Override
	public long available(Article article, long divisor) {
		if (divisor != 1 || article.isNothing()) {
			return 0;
		}

		if (article.isFluid()) {
			return fluid != null && fluid != Fluids.EMPTY && article.toFluid().equals(fluid) ? 1 : 0;
		} else if (article.isItem()) {
			return stack != null && article.matches(stack) ? stack.getCount() : 0;
		} else {
			return 0;
		}
	}

	@Override
	public long supply(Article article, long howMany, long divisor) {
		if (divisor != 1 || article.isNothing()) {
			return 0;
		}

		if (article.isFluid()) {
			if (fluid != null && article.toFluid().equals(fluid)) {
				if (drainable) {
					final ItemStack result = ((BucketPickup) block).pickupBlock(world, pos, blockState);

					if (!result.isEmpty()) {
						fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
						return 1;
					} else {
						return 0;
					}
				} else {
					world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
					fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
					return 1;
				}
			} else {
				return 0;
			}
		} else if (article.isItem()) {
			if (stack != null && article.matches(stack)) {
				final int result = (int) Math.min(stack.getCount(), howMany);
				stack.shrink(result);

				if (stack.isEmpty()) {
					entity.kill();
				} else {
					entity.setItem(stack);
				}

				return result;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
}
