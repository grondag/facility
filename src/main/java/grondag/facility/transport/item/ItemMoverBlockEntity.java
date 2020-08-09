package grondag.facility.transport.item;

import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.BlockComponentContext;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.SingleCarrierProvider;
import grondag.fluidity.wip.base.transport.SubCarrier;
import grondag.xm.api.block.XmProperties;

public abstract class ItemMoverBlockEntity extends PipeBlockEntity implements Tickable, CarrierConnector {
	public static final String TAG_BUFFER = "buffer";
	protected TransportTickHandler tickHandler = this::selectHandler;
	protected BlockPos targetPos = null;
	Direction targetFace = null;
	CarrierSession internalSession;
	protected final TransportBuffer transportBuffer = new TransportBuffer();
	protected TransportStorageContext itemStorage = MissingStorageContext.INSTANCE;

	protected final TransportCarrierContext itemCarrierContext = new TransportCarrierContext(ArticleType.ITEM) {
		@Override
		public CarrierSession session() {
			return internalSession;
		}

		@Override
		public SubCarrier<UtbCostFunction> carrier() {
			return carrier;
		}
	};

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

	public ItemMoverBlockEntity(BlockEntityType<? extends PipeBlockEntity> type) {
		super(type);
		internalSession = carrier.attach(this, ct -> ct.getAccess(this));
	}


	@Override
	protected final CarrierProvider createCarrierProvider() {
		return SingleCarrierProvider.of(carrier);
	}

	// does not provide carrier to the attached block
	@Override
	public final CarrierProvider getCarrierProvider(BlockComponentContext ctx) {
		return world == null || pos == null || ctx.side() == getCachedState().get(XmProperties.FACE) ? CarrierProvider.CARRIER_PROVIDER_COMPONENT.absent() : carrierProvider;
	}

	@Override
	public final Set<ArticleType<?>> articleTypes() {
		return ArticleType.SET_OF_ITEMS;
	}

	protected final boolean selectHandler(TransportContext context) {
		if(getWorld() == null || getPos() == null) {
			return true;
		}

		final Direction face = getCachedState().get(XmProperties.FACE);

		targetPos = getPos().offset(face);
		targetFace = face.getOpposite();

		final Store storage =  Store.STORAGE_COMPONENT.getAccess(world, targetPos).get();

		if(storage != Store.STORAGE_COMPONENT.absent()) {
			itemStorage = new FluidityStorageContext() {
				@Override
				protected Store store() {
					return Store.STORAGE_COMPONENT.getAccess(world, targetPos).get();
				}
			};
		} else {
			final Inventory inv = HopperBlockEntity.getInventoryAt(world, targetPos);

			if(inv != null) {
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
			}
		}

		tickHandler = itemStorage == MissingStorageContext.INSTANCE ? TransportTickHandler.NOOP : itemTickHandler();
		return true;
	}

	@Override
	protected final void onEnquedUpdate() {
		resetTickHandler();
	}

	public final void resetTickHandler() {
		tickHandler = this::selectHandler;
	}

	@Override
	public final void tick() {
		if(world.isClient) {
			return;
		}

		// TODO: allow inversion or disable of redstone control
		if(getCachedState().get(Properties.POWERED)) {
			return;
		}

		itemStorage.prepareForTick();
		tickBuffer();

		if (!tickHandler.tick(itemContext)) {
			resetTickHandler();
		}

		tickBuffer();
	}

	protected abstract void tickBuffer();

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		if (!transportBuffer.state().shouldSave()) {
			tag.put(TAG_BUFFER, transportBuffer.state().toTag());
		}

		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);

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
}
