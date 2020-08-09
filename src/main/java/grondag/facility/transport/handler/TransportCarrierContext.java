package grondag.facility.transport.handler;

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
	public Article targetArticle = Article.NOTHING;
	protected long targetAddress = AssignedNumbersAuthority.INVALID_ADDRESS;
	// set initial value so peer nodes don't all go at once
	protected int cooldownTicks = ThreadLocalRandom.current().nextInt(FacilityConfig.utb1ImporterCooldownTicks);
	protected final ArticleType<?> articleType;

	abstract public CarrierSession session();

	abstract public SubCarrier<UtbCostFunction> carrier();

	protected TransportCarrierContext(ArticleType<?> articleType) {
		this.articleType = articleType;
	}
	public CarrierNode lastTarget() {
		return targetAddress == AssignedNumbersAuthority.INVALID_ADDRESS ? CarrierNode.INVALID : session().carrier().nodeByAddress(targetAddress);
	}

	public CarrierNode randomSource() {
		final CarrierNode result = session().randomPeer();
		targetAddress = result.isValid() ? result.nodeAddress() : AssignedNumbersAuthority.INVALID_ADDRESS;
		return result;
	}

	public CarrierNode sourceFor(Article article) {
		final CarrierNode result = session().supplierOf(article);
		targetAddress = result.isValid() ? result.nodeAddress() : AssignedNumbersAuthority.INVALID_ADDRESS;
		return result;
	}

	public Article randomArticleFromSource(CarrierNode sourceNode) {
		return sourceNode.isValid() ? sourceNode.getComponent(ArticleFunction.SUPPLIER_COMPONENT).get().suggestArticle(articleType) : Article.NOTHING;
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

	public CarrierNode consumerFor(Article article) {
		final CarrierNode result = session().consumerOf(article);
		targetAddress = result.isValid() ? result.nodeAddress() : AssignedNumbersAuthority.INVALID_ADDRESS;
		return result;
	}
}
