package grondag.facility.storage.bulk;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import grondag.facility.Facility;
import grondag.facility.init.TankBlocks;
import grondag.facility.storage.PortableStore;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.bulk.SimpleTank;

public class PortableTankItem extends BlockItem {
	public static final PortableStore DISPLAY_TANK = new  PortableStore(new SimpleTank(Fraction.of(32)).filter(ArticleType.FLUID));

	public PortableTankItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		if(!ctx.getPlayer().isSneaking()) {
			if(use(ctx.getWorld(), ctx.getPlayer(), ctx.getHand()).getResult().isAccepted()) {
				return ActionResult.SUCCESS;
			}
		}

		return super.useOnBlock(ctx);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		final ItemStack itemStack = playerEntity.getStackInHand(hand);

		if(itemStack.getItem() != TankBlocks.PORTABLE_TANK_ITEM) {
			return TypedActionResult.pass(itemStack);
		}

		final Store store = new PortableStore(new SimpleTank(Fraction.of(32)).filter(ArticleType.FLUID), () -> playerEntity.getStackInHand(hand), s -> playerEntity.setStackInHand(hand, s));

		final HitResult hitResult = rayTrace(world, playerEntity, RayTraceContext.FluidHandling.SOURCE_ONLY);

		if (hitResult.getType() == HitResult.Type.MISS) {
			return TypedActionResult.pass(itemStack);
		} else if (hitResult.getType() != HitResult.Type.BLOCK) {
			return TypedActionResult.pass(itemStack);
		} else {
			final BlockHitResult blockHitResult = (BlockHitResult)hitResult;
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

			final Fluid worldFluid = blockState.getFluidState().getFluid();

			if (world.canPlayerModifyAt(playerEntity, onPos)) {
				if (blockState.getBlock() instanceof FluidDrainable && worldFluid != Fluids.EMPTY && store.getConsumer().apply(Article.of(worldFluid), 1, true) == 1) {
					final Fluid fluid = ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, onPos, blockState);

					if (fluid != Fluids.EMPTY) {
						if(store.getConsumer().apply(Article.of(fluid), 1, false) != 1) {
							Facility.LOG.warn("Tank item did not accept fluid when expected. Fluid drained from world has been lost. This is a bug.");
						}

						playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
						world.playSound(playerEntity, onPos, fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

						return TypedActionResult.success(playerEntity.getStackInHand(hand));
					}
				}

				if(store.getSupplier().apply(Article.of(tankFluid), 1, true) == 1) {
					if(worldFluid == Fluids.EMPTY) {
						final BlockPos placePos = blockState.getBlock() instanceof FluidFillable && tankFluid == Fluids.WATER ? onPos : onPos.offset(blockHitResult.getSide());

						if (placeFluid(tankFluid, playerEntity, world, placePos, blockHitResult)) {
							if(store.getSupplier().apply(Article.of(tankFluid), 1, false) != 1) {
								Facility.LOG.warn("Tank item did not supply fluid when expected. Fluid added to world without draining tank. This is a bug.");
							}

							if (playerEntity instanceof ServerPlayerEntity) {
								Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, placePos, itemStack);
							}

							playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
							return TypedActionResult.success(playerEntity.getStackInHand(hand));
						}
					}

					return TypedActionResult.consume(playerEntity.getStackInHand(hand));
				}
			}

			return TypedActionResult.pass(itemStack);
		}
	}

	protected boolean placeFluid(Fluid fluid, @Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
		if (!(fluid instanceof FlowableFluid)) {
			return false;
		} else {
			final BlockState blockState = world.getBlockState(blockPos);
			final Material material = blockState.getMaterial();
			final boolean canPlace = blockState.canBucketPlace(fluid);

			if (!blockState.isAir() && !canPlace && (!(blockState.getBlock() instanceof FluidFillable) || !((FluidFillable)blockState.getBlock()).canFillWithFluid(world, blockPos, blockState, fluid))) {
				return blockHitResult == null ? false : placeFluid(fluid, playerEntity, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), null);
			} else {
				if (world.getDimension().isUltrawarm() && fluid.isIn(FluidTags.WATER)) {
					final int i = blockPos.getX();
					final int j = blockPos.getY();
					final int k = blockPos.getZ();
					world.playSound(playerEntity, blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

					for(int l = 0; l < 8; ++l) {
						world.addParticle(ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
					}
				} else if (blockState.getBlock() instanceof FluidFillable && fluid == Fluids.WATER) {
					if (((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, blockPos, blockState, ((FlowableFluid)fluid).getStill(false))) {
						playEmptyingSound(fluid, playerEntity, world, blockPos);
					}
				} else {
					if (!world.isClient && canPlace && !material.isLiquid()) {
						world.breakBlock(blockPos, true);
					}

					playEmptyingSound(fluid, playerEntity, world, blockPos);
					world.setBlockState(blockPos, fluid.getDefaultState().getBlockState(), 11);
				}

				return true;
			}
		}
	}

	protected void playEmptyingSound(Fluid fluid, @Nullable PlayerEntity playerEntity, World world, BlockPos blockPos) {
		final SoundEvent soundEvent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
		world.playSound(playerEntity, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		super.appendTooltip(itemStack, world, list, tooltipContext);
		// TODO: localize
		DISPLAY_TANK.readFromStack(itemStack);

		if(DISPLAY_TANK.isEmpty()) {
			list.add(new LiteralText("Empty"));
		} else {
			list.add(new LiteralText("Fluid: " + Registry.FLUID.getId(DISPLAY_TANK.view(0).article().toFluid()).toString()));
			list.add(new LiteralText(DISPLAY_TANK.amount().toString()));
		}
	}
}
