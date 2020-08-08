package grondag.facility.transport.item;

import java.util.Set;

import com.google.common.util.concurrent.Runnables;
import io.netty.util.internal.ThreadLocalRandom;

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

import grondag.facility.FacilityConfig;
import grondag.facility.transport.PipeBlockEntity;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.BlockComponentContext;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.base.storage.bulk.SimpleTank;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.SingleCarrierProvider;
import grondag.xm.api.block.XmProperties;

public abstract  class ItemMoverBlockEntity extends PipeBlockEntity implements Tickable, CarrierConnector {
	public static final String TAG_BUFFER = "buffer";
	protected Runnable tickHandler = this::selectRunnable;
	protected BlockPos targetPos = null;
	Direction targetFace = null;
	CarrierSession internalSession;
	// set initial value so peer nodes don't all go at once
	protected int cooldownTicks = ThreadLocalRandom.current().nextInt(FacilityConfig.utb1ImporterCooldownTicks);

	protected final SimpleTank buffer = new SimpleTank(Fraction.MAX_VALUE);

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

	protected abstract void handleStorage();

	protected abstract void handleVanillaInv();

	protected abstract void handleVanillaSidedInv();

	protected final void selectRunnable() {
		if(getWorld() == null || getPos() == null) {
			return;
		}

		final Direction face = getCachedState().get(XmProperties.FACE);

		targetPos = getPos().offset(face);
		targetFace = face.getOpposite();

		final Store storage =  Store.STORAGE_COMPONENT.getAccess(world, targetPos).get();

		if(storage != Store.STORAGE_COMPONENT.absent()) {
			tickHandler = this::handleStorage;
			return;
		}

		final Inventory inv = HopperBlockEntity.getInventoryAt(world, targetPos);

		if(inv != null) {
			tickHandler = inv instanceof SidedInventory ? this::handleVanillaSidedInv : this::handleVanillaInv;
			return;
		}

		tickHandler = Runnables.doNothing();
	}

	@Override
	protected final void onEnquedUpdate() {
		resetTickHandler();
	}

	public final void resetTickHandler() {
		tickHandler = this::selectRunnable;
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

		if(--cooldownTicks <= 0) {
			tickHandler.run();
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		if (!buffer.isEmpty()) {
			tag.put(TAG_BUFFER, buffer.writeTag());
		}

		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);

		if (tag.contains(TAG_BUFFER)) {
			buffer.readTag(tag.getCompound(TAG_BUFFER));
		} else {
			buffer.clear();
		}
	}

	public ArticleFunction getSupplier() {
		return ArticleFunction.ALWAYS_RETURN_ZERO;
	}

	public ArticleFunction getConsumer() {
		return ArticleFunction.ALWAYS_RETURN_ZERO;
	}
}
