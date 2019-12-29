package grondag.facility.block;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import grondag.fermion.modkeys.api.ModKeys;
import grondag.fluidity.api.storage.Storage;
import grondag.fluidity.base.article.StoredDiscreteArticle;
import grondag.fluidity.base.storage.discrete.AbstractDiscreteStorage;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.Species;
import grondag.xm.api.connect.species.SpeciesFunction;
import grondag.xm.api.connect.species.SpeciesMode;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.world.BlockTest;

public class ItemStorageBlock extends Block implements BlockEntityProvider {
	public static final Identifier CONTENTS  = ShulkerBoxBlock.CONTENTS;

	public final SpeciesFunction speciesFunc = SpeciesProperty.speciesForBlock(this);
	protected final Supplier<BlockEntity> beFactory;

	public ItemStorageBlock(Block.Settings settings, Supplier<BlockEntity> beFactory) {
		super(settings);
		this.beFactory = beFactory;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return beFactory.get();
	}

	@Override
	public boolean hasBlockEntity() {
		return true;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(SpeciesProperty.SPECIES);
		builder.add(XmProperties.FACE);
	}

	@SuppressWarnings("rawtypes")
	public static final BlockTest JOIN_TEST = ctx -> canConnect(ctx.fromBlockState(), ctx.toBlockState());

	public static boolean canConnect(BlockState fromState, BlockState toState) {
		return fromState.getBlock() instanceof ItemStorageBlock
				&& toState.getBlock() instanceof ItemStorageBlock
				&& fromState.get(SpeciesProperty.SPECIES) == toState.get(SpeciesProperty.SPECIES);
	}

	public static boolean canConnect(ItemStorageBlockEntity fromEntity, ItemStorageBlockEntity toEntity) {
		final World fromWorld = fromEntity.getWorld();
		return fromWorld == null || fromWorld != toEntity.getWorld() ? false : canConnect(fromEntity.getCachedState(), toEntity.getCachedState());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		final Direction face = context.getPlayerLookDirection();
		final BlockPos onPos = context.getBlockPos().offset(context.getSide().getOpposite());
		final SpeciesMode mode = ModKeys.isPrimaryPressed(context.getPlayer()) ? SpeciesMode.COUNTER_MOST : SpeciesMode.MATCH_MOST;
		final int species = Species.speciesForPlacement(context.getWorld(), onPos, face.getOpposite(), mode, speciesFunc);
		return getDefaultState().with(SpeciesProperty.SPECIES, species).with(XmProperties.FACE, face);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			final BlockEntity be = world.getBlockEntity(pos);

			if(be instanceof ItemStorageBlockEntity) {
				final String label = ((ItemStorageBlockEntity) be).label;

				ContainerProviderRegistry.INSTANCE.openContainer(ItemStorageContainer.ID, player, p -> {
					p.writeBlockPos(pos);
					p.writeString(label);
				});
			}
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		final BlockEntity blockEntity = world.getBlockEntity(blockPos);

		if (blockEntity instanceof ItemStorageBlockEntity) {
			//TODO: move to helper method on storage
			final Storage storage = ((ItemStorageBlockEntity)blockEntity).getLocalStorage();

			if(storage != null){
				return (int)(Math.floor(14.0 * storage.count() / storage.capacity())) + 1;
			}
		}

		return 0;
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		final BlockEntity blockEntity = world.getBlockEntity(blockPos);

		if (blockEntity instanceof ItemStorageBlockEntity) {
			final ItemStorageBlockEntity myBlockEntity = (ItemStorageBlockEntity)blockEntity;

			if (!world.isClient) {
				final ItemStack stack = new ItemStack(this);

				if(!myBlockEntity.getLocalStorage().isEmpty()) {
					final CompoundTag tag = myBlockEntity.toContainerTag(new CompoundTag());

					if (!tag.isEmpty()) {
						stack.putSubTag("BlockEntityTag", tag);
					}

					stack.setCustomName(new LiteralText(myBlockEntity.getLabel()));
				}

				final ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void buildTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
		super.buildTooltip(itemStack, blockView, list, tooltipContext);
		final CompoundTag beTag = itemStack.getSubTag("BlockEntityTag");

		// TODO: move to shared helper method
		if (beTag != null && beTag.contains(ItemStorageBlockEntity.TAG_STORAGE)) {
			final ListTag tagList = beTag.getCompound(ItemStorageBlockEntity.TAG_STORAGE).getList(AbstractDiscreteStorage.TAG_ITEMS, 10);
			final int limit = Math.min(32,tagList.size());
			final StoredDiscreteArticle lookup = new StoredDiscreteArticle();

			for(int i = 0; i < limit; i++) {
				lookup.readTag(tagList.getCompound(i));

				if(!lookup.isEmpty()) {
					final Text text = lookup.article().toStack().getName().deepCopy();
					text.append(" x").append(String.valueOf(lookup.count()));
					list.add(text);
				}
			}

			if(limit < tagList.size()) {
				list.add(new LiteralText("..."));

			}
		}
	}
}