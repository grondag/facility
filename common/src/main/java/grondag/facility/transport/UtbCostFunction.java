package grondag.facility.transport;

import java.util.function.Consumer;

import grondag.facility.FacilityConfig;
import grondag.facility.varia.WorldTaskManager;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.transact.Transaction;
import grondag.fluidity.api.transact.TransactionContext;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.CarrierCostFunction;

public class UtbCostFunction implements CarrierCostFunction {
	/** tick when we last computed limits */
	int lastTick = 0;

	int lastTickSaturationCounter = 0;
	int thisTickSaturationCounter = 0;
	int rotation = 0;

	int balance = FacilityConfig.utb1ItemsPerTick;

	/**
	 * Implements a sort of fairness mechanism and assumes nodes will attempt to
	 * transmit in the same sequence each tick.<p>
	 *
	 * Each call increments a saturation counter and will receive OK to transmit
	 * only if network was not saturated last tick or if counter matches a rotating
	 * index value.
	 *
	 * @return true if node should transmit
	 */
	public boolean shouldTransmit() {
		refresh();
		Transaction.selfEnlistIfOpen(this);
		++thisTickSaturationCounter;
		return lastTickSaturationCounter == 1 ? true : thisTickSaturationCounter % lastTickSaturationCounter == rotation;
	}

	protected void refresh() {
		final int thisTick = WorldTaskManager.tickCounter();
		final int perTick = FacilityConfig.utb1ItemsPerTick;

		if(thisTick > lastTick) {
			balance += (thisTick - lastTick) * perTick;

			if(balance > perTick) {
				balance = perTick;
			}

			lastTick = thisTick;

			if(thisTickSaturationCounter <= 1) {
				lastTickSaturationCounter = 1;
			} else {
				lastTickSaturationCounter = thisTickSaturationCounter;
				rotation = thisTick % thisTickSaturationCounter;
			}

			thisTickSaturationCounter = 0;
		}
	}

	private final Consumer<TransactionContext> rollbackHandler = ctx -> {
		if (!ctx.isCommited()) {
			final long state = ctx.getState();
			balance = (int) (state & 0xFFFFFFFFL);
			thisTickSaturationCounter = (int) (state >>> 32);
		}
	};

	private final TransactionDelegate txDelegate = ctx -> {
		ctx.setState(((long) thisTickSaturationCounter << 32) | balance);
		return rollbackHandler;
	};

	@Override
	public TransactionDelegate getTransactionDelegate() {
		return txDelegate;
	}

	@Override
	public long apply(CarrierSession sender, Article item, long count, boolean simulate) {
		if(count == 0) {
			return count;
		}

		refresh();

		Transaction.selfEnlistIfOpen(this);

		final long result = balance > 0 ? Math.min(count, balance) : 0;

		if(!simulate && result != 0) {
			balance -= result;
		}

		return result;
	}

	@Override
	public Fraction apply(CarrierSession sender, Article item, Fraction volume, boolean simulate) {
		if(volume.isZero()) {
			return volume.toImmutable();
		}

		refresh();

		Transaction.selfEnlistIfOpen(this);

		final Fraction result = volume.ceil() > balance ? (balance > 0 ? Fraction.of(balance) : Fraction.ZERO) : volume;

		if(!simulate && !result.isZero()) {
			balance -= result.whole();
		}

		return result;
	}

	@Override
	public long apply(CarrierSession sender, Article item, long numerator, long divisor, boolean simulate) {
		if(numerator == 0) {
			return 0;
		}

		refresh();

		Transaction.selfEnlistIfOpen(this);

		final long result = balance > 0 ? Math.min((numerator + divisor - 1) / divisor, balance) : 0;

		if(!simulate && result != 0) {
			balance -= result;
		}

		return result * divisor;
	}
}
