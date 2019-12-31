package grondag.facility.wip.transport.impl;

import java.util.Iterator;

import grondag.facility.wip.transport.Carrier;
import grondag.facility.wip.transport.CarrierEndpoint;
import grondag.facility.wip.transport.CarrierSession;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.fraction.FractionView;
import grondag.fluidity.api.fraction.MutableFraction;
import grondag.fluidity.api.storage.ArticleConsumer;

public class BroadcastConsumer implements ArticleConsumer {
	private final CarrierSession fromNode;

	public BroadcastConsumer(CarrierSession fromNode) {
		this.fromNode = fromNode;
	}

	@Override
	public long accept(Article item, long count, boolean simulate) {
		final Carrier carrier = fromNode.carrier();

		if(carrier.nodeCount() <= 1) {
			return 0;
		}

		long result = 0;

		final Iterator<? extends CarrierEndpoint> it = carrier.nodes().iterator();

		while(it.hasNext()) {
			final CarrierEndpoint n = it.next();

			if(n != fromNode && n.nodeConsumer() != null) {
				result += n.nodeConsumer().accept(item, count - result, simulate);

				if(result >= count) {
					break;
				}
			}
		}

		return result;
	}

	protected final MutableFraction calc = new MutableFraction();
	protected final MutableFraction result = new MutableFraction();

	@Override
	public FractionView accept(Article item, FractionView volume, boolean simulate) {
		final Carrier carrier = fromNode.carrier();

		if(carrier.nodeCount() <= 1) {
			return Fraction.ZERO;
		}

		result.set(0);
		calc.set(volume);

		final Iterator<? extends CarrierEndpoint> it = carrier.nodes().iterator();

		while(it.hasNext()) {
			final CarrierEndpoint n = it.next();

			if(n != fromNode && n.nodeConsumer() != null) {
				final FractionView amt = n.nodeConsumer().accept(item, calc, simulate);

				if(!amt.isZero()) {
					result.add(amt);
					calc.subtract(amt);

					if(result.isGreaterThankOrEqual(volume)) {
						break;
					}
				}
			}
		}

		return result;
	}

	@Override
	public long accept(Article item, long numerator, long divisor, boolean simulate) {
		final Carrier carrier = fromNode.carrier();

		if(carrier.nodeCount() <= 1) {
			return 0;
		}

		long result = 0;

		final Iterator<? extends CarrierEndpoint> it = carrier.nodes().iterator();

		while(it.hasNext()) {
			final CarrierEndpoint n = it.next();

			if(n != fromNode && n.nodeConsumer() != null) {
				result += n.nodeConsumer().accept(item, numerator - result, divisor, simulate);

				if(result >= numerator) {
					break;
				}
			}
		}

		return result;
	}

	@Override
	public TransactionDelegate getTransactionDelegate() {
		//TODO: implement
		return ctx -> null;
	}
}
