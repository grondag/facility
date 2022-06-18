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

package grondag.facility.storage.bulk;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import grondag.facility.Facility;
import grondag.facility.init.TankBlocks;
import grondag.facility.storage.PortableStore;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.bulk.SimpleTank;

public class PortableTankItem extends BlockItem {
	public static final PortableStore DISPLAY_TANK = new PortableStore(new SimpleTank(Fraction.of(32)).filter(ArticleType.FLUID));

	public PortableTankItem(Block block, Properties settings) {
		super(block, settings);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (!ctx.getPlayer().isShiftKeyDown()) {
			if (use(ctx.getLevel(), ctx.getPlayer(), ctx.getHand()).getResult().consumesAction()) {
				return InteractionResult.SUCCESS;
			}
		}

		return super.useOn(ctx);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
		final ItemStack itemStack = playerEntity.getItemInHand(hand);

		if (itemStack.getItem() != TankBlocks.portableTankItem()) {
			return InteractionResultHolder.pass(itemStack);
		}

		final Store store = new PortableStore(new SimpleTank(Fraction.of(32)).filter(ArticleType.FLUID), () -> playerEntity.getItemInHand(hand), s -> playerEntity.setItemInHand(hand, s));

		final HitResult hitResult = getPlayerPOVHitResult(world, playerEntity, ClipContext.Fluid.SOURCE_ONLY);

		if (hitResult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemStack);
		} else if (hitResult.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(itemStack);
		} else {
			final BlockHitResult blockHitResult = (BlockHitResult) hitResult;
			final BlockPos onPos = blockHitResult.getBlockPos();
			final Fluid tankFluid = store.view(0).article().toFluid();
			final BlockState blockState = world.getBlockState(onPos);

			//TODO: mixin to cauldron block?
			// Cauldron onUse method consumes block item uses that aren't shulker boxes
			// when the cauldron is full, so we don't get here in that case.

			//			if(blockState.getBlock() == Blocks.CAULDRON) {
			//				final int cauldronLevel = blockState.get(CauldronBlock.LEVEL);
			//
			//				if(store.isEmpty()) {
			//					if(cauldronLevel > 0) {
			//						final int drained = (int) store.getConsumer().apply(Article.of(Fluids.WATER), cauldronLevel, 3, false);
			//
			//						if(drained != 0) {
			//							world.setBlockState(onPos, blockState.with(CauldronBlock.LEVEL, cauldronLevel - drained));
			//							return TypedActionResult.success(playerEntity.getStackInHand(hand));
			//						}
			//					}
			//				} else if(tankFluid == Fluids.WATER) {
			//					if(cauldronLevel == 3) {
			//						if(store.getConsumer().apply(Article.of(Fluids.WATER), 1, false) == 1) {
			//							world.setBlockState(onPos, blockState.with(CauldronBlock.LEVEL, 0));
			//							return TypedActionResult.success(playerEntity.getStackInHand(hand));
			//						}
			//					} else {
			//						final int filled = (int) store.getSupplier().apply(Article.of(Fluids.WATER),  3 - cauldronLevel, 3, false);
			//
			//						if(filled != 0) {
			//							world.setBlockState(onPos, blockState.with(CauldronBlock.LEVEL, cauldronLevel + filled));
			//							return TypedActionResult.success(playerEntity.getStackInHand(hand));
			//						}
			//					}
			//
			//				}
			//
			//				return TypedActionResult.consume(playerEntity.getStackInHand(hand));
			//			}

			final Fluid worldFluid = blockState.getFluidState().getType();

			if (world.mayInteract(playerEntity, onPos)) {
				if (blockState.getBlock() instanceof BucketPickup && worldFluid != Fluids.EMPTY && store.getConsumer().apply(Article.of(worldFluid), 1, true) == 1) {
					final ItemStack stack = ((BucketPickup) blockState.getBlock()).pickupBlock(world, onPos, blockState);

					if (!stack.isEmpty()) {
						if (store.getConsumer().apply(Article.of(worldFluid), 1, false) != 1) {
							Facility.LOG.warn("Tank item did not accept fluid when expected. Fluid drained from world has been lost. This is a bug.");
						}

						playerEntity.awardStat(Stats.ITEM_USED.get(this));
						world.playSound(playerEntity, onPos, worldFluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);

						return InteractionResultHolder.success(playerEntity.getItemInHand(hand));
					}
				}

				if (store.getSupplier().apply(Article.of(tankFluid), 1, true) == 1) {
					if (worldFluid == Fluids.EMPTY) {
						final BlockPos placePos = blockState.getBlock() instanceof LiquidBlockContainer && tankFluid == Fluids.WATER ? onPos : onPos.relative(blockHitResult.getDirection());

						if (placeFluid(tankFluid, playerEntity, world, placePos, blockHitResult)) {
							if (store.getSupplier().apply(Article.of(tankFluid), 1, false) != 1) {
								Facility.LOG.warn("Tank item did not supply fluid when expected. Fluid added to world without draining tank. This is a bug.");
							}

							if (playerEntity instanceof ServerPlayer) {
								CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerEntity, placePos, itemStack);
							}

							playerEntity.awardStat(Stats.ITEM_USED.get(this));
							return InteractionResultHolder.success(playerEntity.getItemInHand(hand));
						}
					}

					return InteractionResultHolder.consume(playerEntity.getItemInHand(hand));
				}
			}

			return InteractionResultHolder.pass(itemStack);
		}
	}

	protected boolean placeFluid(Fluid fluid, @Nullable Player playerEntity, Level world, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
		if (!(fluid instanceof FlowingFluid)) {
			return false;
		} else {
			final BlockState blockState = world.getBlockState(blockPos);
			final Material material = blockState.getMaterial();
			final boolean canPlace = blockState.canBeReplaced(fluid);

			if (!blockState.isAir() && !canPlace && (!(blockState.getBlock() instanceof LiquidBlockContainer) || !((LiquidBlockContainer) blockState.getBlock()).canPlaceLiquid(world, blockPos, blockState, fluid))) {
				return blockHitResult == null ? false : placeFluid(fluid, playerEntity, world, blockHitResult.getBlockPos().relative(blockHitResult.getDirection()), null);
			} else {
				if (world.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
					final int i = blockPos.getX();
					final int j = blockPos.getY();
					final int k = blockPos.getZ();
					world.playSound(playerEntity, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l) {
						world.addParticle(ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
					}
				} else if (blockState.getBlock() instanceof LiquidBlockContainer && fluid == Fluids.WATER) {
					if (((LiquidBlockContainer) blockState.getBlock()).placeLiquid(world, blockPos, blockState, ((FlowingFluid) fluid).getSource(false))) {
						playEmptyingSound(fluid, playerEntity, world, blockPos);
					}
				} else {
					if (!world.isClientSide && canPlace && !material.isLiquid()) {
						world.destroyBlock(blockPos, true);
					}

					playEmptyingSound(fluid, playerEntity, world, blockPos);
					world.setBlock(blockPos, fluid.defaultFluidState().createLegacyBlock(), 11);
				}

				return true;
			}
		}
	}

	public void playEmptyingSound(Fluid fluid, @Nullable Player playerEntity, Level world, BlockPos blockPos) {
		final SoundEvent soundEvent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		world.playSound(playerEntity, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> list, TooltipFlag tooltipContext) {
		super.appendHoverText(itemStack, world, list, tooltipContext);
		// TODO: localize
		DISPLAY_TANK.readFromStack(itemStack);

		if (DISPLAY_TANK.isEmpty()) {
			list.add(Component.literal("Empty"));
		} else {
			list.add(Component.literal("Fluid: " + Registry.FLUID.getKey(DISPLAY_TANK.view(0).article().toFluid()).toString()));
			list.add(Component.literal(DISPLAY_TANK.amount().toString()));
		}
	}
}
