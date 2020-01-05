package grondag.facility.transport;

import grondag.fermion.world.WorldTaskManager;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.fraction.FractionView;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.CarrierCostFunction;

public class UtbCostFunction implements CarrierCostFunction {
	int lastTick = 0;
	int saturationCounter = 0;
	boolean saturatedThisTick = false;
	CarrierSession firstNodeThisTick = null;

	// TODO: move to config
	long balance = 1;

	public int backoffTickRange() {
		return 1 << saturationCounter;
	}

	protected void refresh() {
		final int thisTick = WorldTaskManager.tickCounter();

		if(thisTick > lastTick) {
			balance += thisTick - lastTick;
			if(balance > 1) {
				balance = 1;
			}

			lastTick = thisTick;
			firstNodeThisTick = null;

			if(saturatedThisTick) {
				++saturationCounter;
				saturatedThisTick = false;
			} else if (saturationCounter > 0) {
				--saturationCounter;
			}
		}
	}

	@Override
	public TransactionDelegate getTransactionDelegate() {
		// TODO implement
		return TransactionDelegate.IGNORE;
	}

	protected void updateSaturation(CarrierSession sender) {
		if(saturatedThisTick) {
			return;
		} else if(firstNodeThisTick == null) {
			firstNodeThisTick = sender;
		} else if (firstNodeThisTick != sender) {
			saturatedThisTick = true;
		}
	}

	@Override
	public long apply(CarrierSession sender, Article item, long count, boolean simulate) {
		if(count == 0) {
			return count;
		}

		refresh();

		final long result = Math.min(count, balance);

		if(!simulate) {
			if(result == 0) {
				updateSaturation(sender);
			} else {
				balance -= result;
			}
		}

		return result;
	}

	@Override
	public FractionView apply(CarrierSession sender, Article item, FractionView volume, boolean simulate) {
		if(volume.isZero()) {
			return volume.toImmutable();
		}

		refresh();

		final FractionView result = volume.ceil() > balance ? Fraction.of(balance) : volume;

		if(!simulate) {
			if(result.isZero()) {
				updateSaturation(sender);
			} else {
				balance -= result.whole();
			}
		}

		return result;
	}

	@Override
	public long apply(CarrierSession sender, Article item, long numerator, long divisor, boolean simulate) {
		if(numerator == 0) {
			return 0;
		}

		refresh();

		final long result = Math.min((numerator + divisor - 1) / divisor, balance);

		if(!simulate) {
			if(result == 0) {
				updateSaturation(sender);
			} else {
				balance -= result;
			}
		}

		return result * divisor;
	}
}
