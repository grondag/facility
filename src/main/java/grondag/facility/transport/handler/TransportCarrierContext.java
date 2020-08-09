package grondag.facility.transport.handler;

import javax.annotation.Nullable;

import io.netty.util.internal.ThreadLocalRandom;

import grondag.facility.FacilityConfig;
import grondag.facility.transport.UtbCostFunction;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.wip.api.transport.CarrierNode;
import grondag.fluidity.wip.api.transport.CarrierSession;
import grondag.fluidity.wip.base.transport.AssignedNumbersAuthority;
import grondag.fluidity.wip.base.transport.SubCarrier;

public abstract class TransportCarrierContext {
	private long consumerAddress = AssignedNumbersAuthority.INVALID_ADDRESS;
	private long supplierAddress = AssignedNumbersAuthority.INVALID_ADDRESS;
	private Article lastAnyAvailable = Article.NOTHING;

	// set initial value so peer nodes don't all go at once
	protected int cooldownTicks = ThreadLocalRandom.current().nextInt(FacilityConfig.utb1ImporterCooldownTicks);
	protected final ArticleType<?> articleType;

	abstract public CarrierSession session();

	abstract public SubCarrier<UtbCostFunction> carrier();

	protected TransportCarrierContext(ArticleType<?> articleType) {
		this.articleType = articleType;
	}

	private CarrierNode lastConsumer() {
		return consumerAddress == AssignedNumbersAuthority.INVALID_ADDRESS ? CarrierNode.INVALID : session().carrier().nodeByAddress(consumerAddress);
	}

	private CarrierNode lastSupplier() {
		return supplierAddress == AssignedNumbersAuthority.INVALID_ADDRESS ? CarrierNode.INVALID : session().carrier().nodeByAddress(supplierAddress);
	}

	public @Nullable ArticleFunction sourceFor(Article article) {
		ArticleFunction result = null;
		CarrierNode node = lastSupplier();

		if (node.isValid()) {
			result = node.getComponent(ArticleFunction.SUPPLIER_COMPONENT).get();

			if (!result.canApply(article)) {
				result = null;
				supplierAddress = AssignedNumbersAuthority.INVALID_ADDRESS;
			}
		}

		if (result == null) {
			node = session().supplierOf(article);

			if (node.isValid()) {
				result = node.getComponent(ArticleFunction.SUPPLIER_COMPONENT).get();

				if(result.canApply(article)) {
					// save for next tick
					supplierAddress = node.nodeAddress();
				} else {
					supplierAddress = AssignedNumbersAuthority.INVALID_ADDRESS;
					result = null;
				}
			}
		}

		return result;
	}

	public long throttle(Article article, long numerator, long denominator, boolean simulate)  {
		return carrier().costFunction().apply(session(), article, numerator, denominator, simulate);
	}

	public void resetCooldown() {
		cooldownTicks = FacilityConfig.utb1ImporterCooldownTicks;
	}

	public boolean isReady() {
		return --cooldownTicks <= 0;
	}

	public @Nullable ArticleFunction consumerFor(Article article) {
		if (article == null || article.isNothing() || article.type() != articleType) {
			return null;
		}

		CarrierNode node = lastConsumer();
		ArticleFunction result = null;

		if (node.isValid()) {
			result = node.getComponent(ArticleFunction.CONSUMER_COMPONENT).get();

			if(!result.canApply(article)) {
				consumerAddress = AssignedNumbersAuthority.INVALID_ADDRESS;
				result = null;
			}
		}

		if (result == null) {
			node = session().consumerOf(article);

			if (node.isValid()) {
				result = node.getComponent(ArticleFunction.CONSUMER_COMPONENT).get();

				if(result.canApply(article)) {
					// save for next tick
					consumerAddress = node.nodeAddress();
				} else {
					consumerAddress = AssignedNumbersAuthority.INVALID_ADDRESS;
					result = null;
				}
			}
		}

		return result;
	}

	/**
	 * Will return last non-nothing result so long as it remains available and until {@link #resetAvailableArticle()} is called.
	 * @return
	 */
	public Article anyAvailableArticle() {
		Article result = lastAnyAvailable;

		if (!result.isNothing()) {
			final ArticleFunction source = sourceFor(result);

			// clear if no longer available
			if (source == null) {
				result = Article.NOTHING;
			}
		}

		if (result.isNothing()) {
			final CarrierNode node = session().randomPeer();

			if (node.isValid()) {
				result = node.getComponent(ArticleFunction.SUPPLIER_COMPONENT).get().suggestArticle(articleType);

				if (!result.isNothing()) {
					lastAnyAvailable = result;
					supplierAddress = node.nodeAddress();
				}
			}
		}

		return result;
	}

	public void resetAvailableArticle() {
		lastAnyAvailable = Article.NOTHING;
	}
}
