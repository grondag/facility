package grondag.facility.transport.item;

import java.util.Set;

import com.google.common.util.concurrent.Runnables;
import io.netty.util.internal.ThreadLocalRandom;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import grondag.facility.FacilityConfig;
import grondag.facility.transport.PipeBlockEntity;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.BlockComponentContext;
import grondag.fluidity.api.storage.Store;
import grondag.fluidity.wip.api.transport.CarrierConnector;
import grondag.fluidity.wip.api.transport.CarrierProvider;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.SingleCarrierProvider;
import grondag.xm.api.block.XmProperties;

public abstract  class ItemMoverBlockEntity extends PipeBlockEntity implements Tickable, CarrierConnector {
	protected Runnable tickHandler = this::selectRunnable;
	protected BlockPos targetPos = null;
	Direction targetFace = null;
	CarrierSession internalSession;
	// set initial value so peer nodes don't all go at once
	protected int cooldownTicks = ThreadLocalRandom.current().nextInt(FacilityConfig.utb1ImporterCooldownTicks);

	public ItemMoverBlockEntity(BlockEntityType<? extends PipeBlockEntity> type) {
		super(type);
		internalSession = carrier.attach(this, ct -> ct.getAccess(this));
	}

	@Override
	protected final CarrierProvider createCarrierProvider() {
		return SingleCarrierProvider.of(carrier);
	}

	// does not provide carrier to the export side
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
}
