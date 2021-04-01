package grondag.facility.transport.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import grondag.facility.storage.TickableBlockEntity;
import grondag.facility.transport.PipeBlockEntity;
import grondag.facility.transport.UtbCostFunction;
import grondag.facility.transport.buffer.TransportBuffer;
import grondag.facility.transport.handler.TransportCarrierContext;
import grondag.facility.transport.handler.TransportContext;
import grondag.facility.transport.handler.TransportTickHandler;
import grondag.facility.transport.storage.FluidityStorageContext;
import grondag.facility.transport.storage.InventoryStorageContext;
import grondag.facility.transport.storage.MissingStorageContext;
import grondag.facility.transport.storage.SidedInventoryStorageContext;
import grondag.facility.transport.storage.TransportStorageContext;
import grondag.facility.transport.storage.WorldStorageContext;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.BlockComponentContext;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.SubCarrier;
import grondag.xm.api.block.XmProperties;

public abstract class ItemMoverBlockEntity extends PipeBlockEntity implements TickableBlockEntity {
	public static final String TAG_BUFFER = "buffer";
	protected BlockPos targetPos = null;
	Direction targetFace = null;
	CarrierSession internalSession;
	protected boolean resetTickHandler = true;

	/** may be used for fluid and/or item or not used at all */
	protected final TransportStorageContext fluidityStorage = new FluidityStorageContext() {
		@Override
		protected Store store() {
			return Store.STORAGE_COMPONENT.getAccess(world, targetPos).get();
		}
	};

	protected final TransportStorageContext worldStorage = new WorldStorageContext() {
		@Override
		protected World world() {
			return getWorld();
		}

		@Override
		protected BlockPos pos() {
			return targetPos;
		}
	};

	protected final TransportBuffer transportBuffer = new TransportBuffer();

	protected TransportTickHandler itemTickHandler = TransportTickHandler.NOOP;
	protected TransportStorageContext itemStorage = MissingStorageContext.INSTANCE;
	protected final TransportCarrierContext itemCarrierContext = new CarrierContext(ArticleType.ITEM);

	protected final TransportContext itemContext = new TransportContext() {
		@Override
		public TransportBuffer buffer() {
			return transportBuffer;
		}

		@Override
		public TransportStorageContext storageContext() {
			return itemStorage;
		}

		@Override
		public TransportCarrierContext carrierContext() {
			return itemCarrierContext;
		}
	};

	protected TransportTickHandler fluidTickHandler = TransportTickHandler.NOOP;
	protected final TransportCarrierContext fluidCarrierContext = new CarrierContext(ArticleType.FLUID);
	protected TransportStorageContext fluidStorage = MissingStorageContext.INSTANCE;

	protected final TransportContext fluidContext = new TransportContext() {
		@Override
		public TransportBuffer buffer() {
			return transportBuffer;
		}

		@Override
		public TransportStorageContext storageContext() {
			return fluidStorage;
		}

		@Override
		public TransportCarrierContext carrierContext() {
			return fluidCarrierContext;
		}
	};

	private class CarrierContext extends TransportCarrierContext {
		protected CarrierContext(ArticleType<?> articleType) {
			super(articleType);
		}

		@Override
		public CarrierSession session() {
			return internalSession;
		}

		@Override
		public SubCarrier<UtbCostFunction> carrier() {
			return carrier;
		}
	}

	public ItemMoverBlockEntity(BlockEntityType<? extends PipeBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		internalSession = carrier.attach(ct -> ct.getAccess(this));
	}

	// does not provide carrier to the attached block
	@Override
	public final CarrierProvider getCarrierProvider(BlockComponentContext ctx) {
		return ctx.side() == ctx.blockState().get(XmProperties.FACE) ? CarrierProvider.CARRIER_PROVIDER_COMPONENT.absent() : carrierProvider;
	}

	protected final void selectHandler() {
		if(getWorld() == null || getPos() == null) {
			return;
		}

		resetTickHandler = false;

		final Direction face = getCachedState().get(XmProperties.FACE);
		targetPos = getPos().method_35851(face);
		targetFace = face.getOpposite();

		final Store storage =  Store.STORAGE_COMPONENT.getAccess(world, targetPos).get();

		if(storage != Store.STORAGE_COMPONENT.absent()) {
			fluidStorage = storage.allowsType(ArticleType.FLUID).mayBeTrue ? fluidityStorage : MissingStorageContext.INSTANCE;
			itemStorage = storage.allowsType(ArticleType.ITEM).mayBeTrue ? fluidityStorage : MissingStorageContext.INSTANCE;
		} else {
			final Inventory inv = HopperBlockEntity.getInventoryAt(world, targetPos);

			if(inv != null) {
				fluidStorage = MissingStorageContext.INSTANCE;

				if (inv instanceof SidedInventory) {
					itemStorage = new SidedInventoryStorageContext(targetFace) {
						@Override
						protected SidedInventory inventory() {
							return (SidedInventory) HopperBlockEntity.getInventoryAt(world, targetPos);
						}
					};
				} else {
					itemStorage = new InventoryStorageContext<Inventory>() {
						@Override
						protected Inventory inventory() {
							return HopperBlockEntity.getInventoryAt(world, targetPos);
						}
					};
				}
			}  else {
				final BlockState state = getWorld().getBlockState(targetPos);
				final Block block = state.getBlock();
				itemStorage = state.isFullCube(world, targetPos) ? MissingStorageContext.INSTANCE : worldStorage;

				if (state.isAir() || (state.getBlock() instanceof FluidBlock && state.getFluidState().isStill())
						|| block instanceof FluidDrainable || block instanceof FluidFillable) {
					fluidStorage = worldStorage;
				} else {
					fluidStorage = MissingStorageContext.INSTANCE;
				}
			}
		}

		itemTickHandler = itemStorage == MissingStorageContext.INSTANCE ? TransportTickHandler.NOOP : itemTickHandler();
		fluidTickHandler = fluidStorage == MissingStorageContext.INSTANCE ? TransportTickHandler.NOOP : fluidTickHandler();
	}

	@Override
	protected final void onEnquedUpdate() {
		resetTickHandler = true;
	}

	@Override
	public final void tick() {
		// TODO: allow inversion or disable of redstone control
		if(getCachedState().get(Properties.POWERED)) {
			return;
		}

		if (resetTickHandler) {
			selectHandler();
		}

		if (!itemTickHandler.tick(itemContext)) {
			resetTickHandler = true;
		}

		if (!fluidTickHandler.tick(fluidContext)) {
			resetTickHandler = true;
		}

		tickBuffer();
	}

	protected abstract void tickBuffer();

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);

		if (!transportBuffer.state().shouldSave()) {
			tag.put(TAG_BUFFER, transportBuffer.state().toTag());
		}

		return tag;
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);

		if (tag.contains(TAG_BUFFER)) {
			transportBuffer.state().fromTag(tag.getCompound(TAG_BUFFER));
		} else {
			transportBuffer.state().reset();
		}
	}

	public ArticleFunction getSupplier() {
		return ArticleFunction.ALWAYS_RETURN_ZERO;
	}

	public ArticleFunction getConsumer() {
		return ArticleFunction.ALWAYS_RETURN_ZERO;
	}

	protected abstract TransportTickHandler itemTickHandler();

	protected abstract TransportTickHandler fluidTickHandler();
}
