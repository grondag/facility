package grondag.facility.transport;

import grondag.fermion.world.WorldTaskManager;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.fraction.FractionView;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.CarrierCostFunction;

public class UtbCostFunction implements CarrierCostFunction {
	int lastTick = 0;
	int lastTickSaturationCounter = 0;
	int thisTickSaturationCounter = 0;
	int rotation = 0;

	// TODO: move to config
	long balance = 1;

	public boolean shouldTransmit() {
		refresh();
		++thisTickSaturationCounter;
		return lastTickSaturationCounter == 1 ? true : thisTickSaturationCounter % lastTickSaturationCounter == rotation;
	}

	protected void refresh() {
		final int thisTick = WorldTaskManager.tickCounter();

		if(thisTick > lastTick) {
			balance += thisTick - lastTick;
			if(balance > 1) {
				balance = 1;
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

	@Override
	public TransactionDelegate getTransactionDelegate() {
		// TODO implement
		return TransactionDelegate.IGNORE;
	}

	@Override
	public long apply(CarrierSession sender, Article item, long count, boolean simulate) {
		if(count == 0) {
			return count;
		}

		refresh();

		final long result = balance > 0 ? Math.min(count, balance) : 0;

		if(!simulate && result != 0) {
			balance -= result;
		}

		return result;
	}

	@Override
	public FractionView apply(CarrierSession sender, Article item, FractionView volume, boolean simulate) {
		if(volume.isZero()) {
			return volume.toImmutable();
		}

		refresh();

		final FractionView result = volume.ceil() > balance ? (balance > 0 ? Fraction.of(balance) : Fraction.ZERO) : volume;

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

		final long result = balance > 0 ? Math.min((numerator + divisor - 1) / divisor, balance) : 0;

		if(!simulate && result != 0) {
			balance -= result;
		}

		return result * divisor;
	}
}
