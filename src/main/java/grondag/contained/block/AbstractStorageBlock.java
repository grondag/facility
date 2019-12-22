package grondag.contained.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import grondag.xm.api.block.XmProperties;
import grondag.xm.api.connect.species.Species;
import grondag.xm.api.connect.species.SpeciesFunction;
import grondag.xm.api.connect.species.SpeciesMode;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.connect.world.BlockTest;

public abstract class AbstractStorageBlock extends Block implements BlockEntityProvider {
	public final SpeciesFunction speciesFunc = SpeciesProperty.speciesForBlock(this);

	public AbstractStorageBlock(Settings settings) {
		super(settings);
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
	public static final BlockTest JOIN_TEST = ctx -> {
		return ctx.fromBlockState().getBlock() == ctx.toBlockState().getBlock()
				&& ctx.fromBlockState().contains(SpeciesProperty.SPECIES)
				&& ctx.fromBlockState().get(SpeciesProperty.SPECIES) == ctx.toBlockState().get(SpeciesProperty.SPECIES)
				&& ctx.fromBlockState().contains(XmProperties.FACE)
				&& ctx.fromBlockState().get(XmProperties.FACE) == ctx.toBlockState().get(XmProperties.FACE);
	};

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		final Direction face = context.getPlayerLookDirection();
		final BlockPos onPos = context.getBlockPos().offset(context.getSide().getOpposite());
		final SpeciesMode mode = context.getPlayer().isSneaking()
				? SpeciesMode.COUNTER_MOST : SpeciesMode.MATCH_MOST;
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

}
