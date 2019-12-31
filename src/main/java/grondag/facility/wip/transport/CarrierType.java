package grondag.facility.wip.transport;

import java.util.Collections;
import java.util.Set;

import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;

@FunctionalInterface
public interface CarrierType {

	Set<ArticleType<?>> articleTypes();

	default Set<Article> whiteList() {
		return Collections.emptySet();
	}

	default Set<Article> blackList() {
		return Collections.emptySet();
	}

	default boolean canCarry(ArticleType<?> type) {
		return articleTypes().contains(type);
	}

	default boolean canCarry(Article article) {
		return canCarry(article.type())
				&& (whiteList().isEmpty() || whiteList().contains(article))
				&& (blackList().isEmpty() || !blackList().contains(article));
	}

	CarrierType EMPTY = () -> Collections.emptySet();

}
