package grondag.facility.wip.transport;

import java.util.Collections;
import java.util.Set;

import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.device.ComponentType;

/**
 * Device that may attach to a carrier.
 */
@FunctionalInterface
public interface CarrierNode {
	Set<ArticleType<?>> articleTypes();

	default String name() {
		return "Unknown";
	}

	CarrierNode EMPTY  = Collections::emptySet;

	ComponentType<CarrierNode> CARRIER_NODE_COMPONENT = () -> EMPTY;
}