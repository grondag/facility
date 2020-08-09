package grondag.facility.transport;

import grondag.facility.FacilityConfig;
import grondag.fluidity.api.article.Article;

public class UtbHelper {
	/**
	 * Limits local transfer operations (buffer to/from storage)
	 */
	public static long throttleUtb1LocalItem(long qty) {
		return Math.min(qty, FacilityConfig.utb1ItemsPerTick);
	}

	/**
	 * Limits local transfer operations (buffer to/from storage)
	 */
	public static long throttleUtb1LocalFluid(long numerator, long divisor) {
		return  Math.min(numerator, divisor);
	}

	public static long throttleUtb1Local(Article article, long numerator, long divisor) {
		if (article.isItem()  && divisor  == 1) {
			return throttleUtb1LocalItem(numerator);
		} else if (article.isFluid()) {
			return throttleUtb1LocalFluid(numerator, divisor);
		} else {
			return 0;
		}
	}
}
