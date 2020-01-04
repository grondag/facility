package grondag.facility.transport;

import grondag.fermion.world.WorldTaskManager;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.fraction.FractionView;
import grondag.fluidity.api.storage.ArticleFunction;

public class UtbCostFunction implements ArticleFunction {
	int lastTick = 0;
	// TODO: move to config
	long balance = 1;

	protected void refresh() {
		final int thisTick = WorldTaskManager.tickCounter();

		if(thisTick > lastTick) {
			balance += thisTick - lastTick;
			if(balance > 1) {
				balance = 1;
			}

			lastTick = thisTick;
		}
	}

	@Override
	public TransactionDelegate getTransactionDelegate() {
		// TODO implement
		return TransactionDelegate.IGNORE;
	}

	@Override
	public long apply(Article item, long count, boolean simulate) {
		refresh();

		final long result = Math.min(count, balance);

		if(!simulate && result != 0) {
			balance -= result;
		}

		return result;
	}

	@Override
	public FractionView apply(Article item, FractionView volume, boolean simulate) {
		refresh();

		final FractionView result = volume.ceil() > balance ? Fraction.of(balance) : volume;

		if(!simulate && !result.isZero()) {
			balance -= result.whole();
		}

		return result;
	}

	@Override
	public long apply(Article item, long numerator, long divisor, boolean simulate) {
		refresh();

		final long result = Math.min((numerator + divisor - 1) / divisor, balance);

		if(!simulate && result != 0) {
			balance -= result;
		}

		return result * divisor;
	}
}
