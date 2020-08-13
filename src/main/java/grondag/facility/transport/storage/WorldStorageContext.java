package grondag.facility.transport.storage;

import java.util.Random;

import com.google.common.base.Predicates;
import io.netty.util.internal.ThreadLocalRandom;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import grondag.facility.FacilityConfig;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;

public abstract class WorldStorageContext implements TransportStorageContext {
	protected abstract World world();

	protected static final int FLUID_COOLDOWN_TICKS = 64 / FacilityConfig.utb1ItemsPerTick;

	protected abstract BlockPos pos();

	protected World world;
	protected BlockPos pos;
	protected BlockState blockState;
	protected Block block;

	Fluid fluid = null;
	ItemStack stack = null;
	ItemEntity entity = null;
	boolean canDropItems = false;
	boolean canPlaceFluid = false;
	Box bounds = null;
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
			bounds = new Box(pos);
		}

		final BlockState state = world.getBlockState(pos);
		blockState = state;
		final Block block = state.getBlock();
		this.block = block;

		if (--fluidCooldownTicks <= 0) {
			final FluidState fluidState = state.getFluidState();
			final Fluid worldFluid = fluidState.getFluid();
			fluid = null;


			if (worldFluid == Fluids.EMPTY) {
				if (state.isAir() && state.getMaterial().isReplaceable()) {
					canPlaceFluid = true;
				} else if (block instanceof FluidFillable) {
					canPlaceFluid = true;
					fillable = true;
				}
			} else if (worldFluid.isStill(fluidState)) {
				if (block instanceof FluidBlock) {
					fluid = worldFluid;
					drainable = false;
				} else if (block instanceof FluidDrainable) {
					fluid = worldFluid;
					drainable = true;
				}
			}
		}

		canDropItems = !state.isFullCube(world, pos);

		final WorldChunk worldChunk = world.getChunkManager().getWorldChunk(pos.getX() >> 4, pos.getZ() >> 4, false);

		if (worldChunk != null) {
			entityList.clear();
			worldChunk.getEntities(ItemEntity.class, bounds, entityList, Predicates.alwaysTrue());
			entity = entityList.isEmpty() ? null : (ItemEntity) entityList.get(0);
			stack = entity == null ? null : entity.getStack();
		}

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
				return  null;
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
					return ((FluidFillable) block).canFillWithFluid(world, pos, blockState, article.toFluid()) ? 1 : 0;
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
			if(canPlaceFluid && (!fillable || ((FluidFillable) block).canFillWithFluid(world, pos, blockState, article.toFluid()))) {
				if (world.getDimension().isUltrawarm() && article.toFluid().isIn(FluidTags.WATER)) {
					final int i = pos.getX();
					final int j = pos.getY();
					final int k = pos.getZ();
					world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

					for(int l = 0; l < 8; ++l) {
						world.addParticle(ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
					}

					fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
					return 1;
				} else {
					if (fillable) {
						if (((FluidFillable) block).tryFillWithFluid(world, pos, blockState, article.toFluid().getDefaultState())) {
							fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
							return 1;
						} else {
							return 0;
						}
					}  else  {
						world.setBlockState(pos, article.toFluid().getDefaultState().getBlockState());
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

					for (int i  = 0; i < limit; ++i)  {
						final ItemEntity e  = (ItemEntity) entityList.get(i);
						final ItemStack  entityStack =  e.getStack();

						if (article.matches(entityStack) && entityStack.getCount() < entityStack.getMaxCount()) {
							final int qty = Math.min(remaining, entityStack.getMaxCount() - entityStack.getCount());
							entityStack.increment(qty);
							e.setStack(entityStack);
							remaining -=  qty;

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

					itemEntity.setVelocity(0, 0, 0);
					world.spawnEntity(itemEntity);
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
			if(fluid != null && article.toFluid().equals(fluid)) {
				if (drainable) {
					final Fluid result = ((FluidDrainable) block).tryDrainFluid(world, pos, blockState);

					if (article.toFluid().equals(result)) {
						fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
						return 1;
					} else {
						return 0;
					}
				} else  {
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
					fluidCooldownTicks = FLUID_COOLDOWN_TICKS;
					return 1;
				}
			} else {
				return 0;
			}
		} else if (article.isItem()) {
			if (stack != null && article.matches(stack)) {
				final int result = (int) Math.min(stack.getCount(), howMany);
				stack.decrement(result);

				if (stack.isEmpty()) {
					entity.kill();
				} else {
					entity.setStack(stack);
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
