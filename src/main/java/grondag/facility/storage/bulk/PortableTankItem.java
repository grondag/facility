package grondag.facility.storage.bulk;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.BaseFluid;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.api.transact.Transaction;

public class PortableTankItem extends BlockItem {
	public static final PortableTank DISPLAY_TANK = new  PortableTank();

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

		if(world.isClient) {
			return TypedActionResult.pass(itemStack);
		}

		final Store store = Store.STORAGE_COMPONENT.getAccessForHeldItem(() -> playerEntity.getStackInHand(hand), s -> playerEntity.setStackInHand(hand, s), (ServerPlayerEntity) playerEntity).get();

		if(store == Store.STORAGE_COMPONENT.absent()) {
			return TypedActionResult.pass(itemStack);
		}

		final HitResult hitResult = rayTrace(world, playerEntity, RayTraceContext.FluidHandling.SOURCE_ONLY);

		if (hitResult.getType() == HitResult.Type.MISS) {
			return TypedActionResult.pass(itemStack);
		} else if (hitResult.getType() != HitResult.Type.BLOCK) {
			return TypedActionResult.pass(itemStack);
		} else {
			final BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			final BlockPos onPos = blockHitResult.getBlockPos();
			final Direction direction = blockHitResult.getSide();
			final BlockPos inPos = onPos.offset(direction);
			final Fluid tankFluid = store.view(0).article().toFluid();

			if (world.canPlayerModifyAt(playerEntity, onPos) && playerEntity.canPlaceOn(inPos, direction, itemStack)) {

				final BlockState blockState = world.getBlockState(onPos);

				if (!store.isFull()) {
					if (blockState.getBlock() instanceof FluidDrainable) {
						final Fluid fluid = ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, onPos, blockState);

						if (fluid != Fluids.EMPTY && store.getConsumer().apply(Article.of(fluid), 1, false) == 1) {

							playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
							world.playSound(null, onPos, fluid.matches(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

							return TypedActionResult.success(playerEntity.getStackInHand(hand));
						}
					}
				}

				final BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable && tankFluid == Fluids.WATER ? onPos : inPos;

				try(Transaction tx = Transaction.open()) {
					tx.enlist(store);

					if(store.getSupplier().apply(Article.of(tankFluid), 1, false) == 1) {
						if (placeFluid(tankFluid, playerEntity, world, blockPos3, blockHitResult)) {
							tx.commit();

							if (playerEntity instanceof ServerPlayerEntity) {
								Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos3, itemStack);
							}

							playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
							return TypedActionResult.success(playerEntity.getStackInHand(hand));
						} else {
							tx.rollback();
						}
					}
				}
			}

			return TypedActionResult.fail(itemStack);
		}
	}

	protected boolean placeFluid(Fluid fluid, @Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
		if (!(fluid instanceof BaseFluid)) {
			return false;
		} else {
			final BlockState blockState = world.getBlockState(blockPos);
			final Material material = blockState.getMaterial();
			final boolean bl = blockState.canBucketPlace(fluid);

			if (!blockState.isAir() && !bl && (!(blockState.getBlock() instanceof FluidFillable) || !((FluidFillable)blockState.getBlock()).canFillWithFluid(world, blockPos, blockState, fluid))) {
				return blockHitResult == null ? false : placeFluid(fluid, playerEntity, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), (BlockHitResult)null);
			} else {
				if (world.dimension.doesWaterVaporize() && fluid.matches(FluidTags.WATER)) {
					final int i = blockPos.getX();
					final int j = blockPos.getY();
					final int k = blockPos.getZ();
					world.playSound(null, blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

					for(int l = 0; l < 8; ++l) {
						world.addParticle(ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
					}
				} else if (blockState.getBlock() instanceof FluidFillable && fluid == Fluids.WATER) {
					if (((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, blockPos, blockState, ((BaseFluid)fluid).getStill(false))) {
						playEmptyingSound(fluid, playerEntity, world, blockPos);
					}
				} else {
					if (!world.isClient && bl && !material.isLiquid()) {
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
		final SoundEvent soundEvent = fluid.matches(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
		world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		super.appendTooltip(itemStack, world, list, tooltipContext);
		// TODO: localize
		DISPLAY_TANK.readFromStack(itemStack);
		list.add(new LiteralText(DISPLAY_TANK.amount().toString()));
	}
}
