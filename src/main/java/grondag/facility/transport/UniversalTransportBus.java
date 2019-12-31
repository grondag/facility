package grondag.facility.transport;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import grondag.facility.wip.transport.CarrierType;
import grondag.fluidity.api.article.ArticleType;

public class UniversalTransportBus implements CarrierType {
	protected static final Set<ArticleType<?>> articleTypes = ImmutableSet.of(ArticleType.ITEM, ArticleType.FLUID);

	@Override
	public Set<ArticleType<?>> articleTypes() {
		return articleTypes;
	}

	public static UniversalTransportBus BASIC = new UniversalTransportBus();

	public static Set<CarrierType> SET_OF_BASIC = ImmutableSet.of(BASIC);
}
