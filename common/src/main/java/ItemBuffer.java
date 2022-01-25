/*
 * This file is part of Facility and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//package grondag.facility.transport.buffer;
//
//import java.util.function.Consumer;
//
//import net.minecraft.nbt.CompoundTag;
//
//import grondag.facility.transport.handler.TransportCarrierContext;
//import grondag.facility.transport.storage.TransportStorageContext;
//import grondag.fluidity.api.article.Article;
//import grondag.fluidity.api.storage.ArticleFunction;
//import grondag.fluidity.api.transact.Transaction;
//import grondag.fluidity.api.transact.TransactionContext;
//import grondag.fluidity.wip.api.transport.CarrierNode;
//
//public class ItemBuffer extends TransportBuffer {
//	long quantity = 0;
//	long rollbackQuantity = 0;
//
//	@Override
//	public long accept(Article article, long numerator, long divisor, boolean simulate) {
//		if (quantity == 0 || article.equals(this.article)) {
//			if (!simulate) {
//				this.article = article;
//				quantity += numerator;
//			}
//
//			return numerator;
//		} else {
//			return 0;
//		}
//	}
//
//	@Override
//	public long supply(Article article, long numerator, long divisor, boolean simulate) {
//		if (quantity == 0 || !article.equals(this.article)) {
//			return 0;
//		} else {
//			final long amt = Math.min(numerator, quantity);
//
//			if (!simulate) {
//				quantity -= amt;
//			}
//
//			return amt;
//		}
//	}
//
//	@Override
//	public boolean clearBuffer(TransportStorageContext context) {
//		if (quantity == 0) {
//			return true;
//		} else {
//			final long qty = context.accept(article, quantity, 1);
//			assert qty <= quantity;
//			assert qty >= 0;
//
//			quantity -= qty;
//
//			return quantity == 0;
//		}
//	}
//
//	@Override
//	public boolean clearBuffer(TransportCarrierContext carrierContext) {
//		if (quantity == 0) {
//			return true;
//		}
//
//		if(!carrierContext.isReady()) {
//			return false;
//		}
//
//		CarrierNode targetNode = carrierContext.lastTarget();
//
//		if (!targetNode.isValid() || !targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get().canApply(article)) {
//			targetNode = carrierContext.consumerFor(article);
//		}
//
//		if (!targetNode.isValid()) {
//			return false;
//		}
//
//		final ArticleFunction consumer = targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get();
//
//		assert Transaction.current() == null;
//
//		try (Transaction tx = Transaction.open()) {
//			tx.enlist(consumer);
//			tx.enlist(this);
//
//			long howMany = carrierContext.throttle(article, quantity, 1, false);
//			howMany = consumer.apply(article, howMany, 1, false);
//
//			assert howMany >= 0;
//			assert howMany <= quantity;
//
//			if (howMany > 0) {
//				quantity -= howMany;
//				tx.commit();
//				carrierContext.resetCooldown();
//			}
//		}
//
//		assert quantity >= 0;
//
//		return quantity == 0;
//	}
//
//	@Override
//	public CompoundTag toTag() {
//		final CompoundTag tag = new CompoundTag();
//		tag.putLong("qty", quantity);
//		tag.put("art", article.toTag());
//		return tag;
//	}
//
//	@Override
//	public void fromTag(CompoundTag tag) {
//		article = Article.fromTag(tag.get("art"));
//		quantity = tag.getLong("qty");
//	}
//
//	protected final Consumer<TransactionContext> rollbackHandler = c -> {
//		if (!c.isCommited()) {
//			article = rollBackArticle;
//			quantity = rollbackQuantity;
//		}
//	};
//
//	@Override
//	public Consumer<TransactionContext> prepareRollback(TransactionContext context) {
//		rollBackArticle = article;
//		rollbackQuantity = quantity;
//		return rollbackHandler;
//	}
//
//	@Override
//	protected Object createRollbackState() {
//		return null;
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return quantity == 0;
//	}
//
//	@Override
//	public void reset() {
//		quantity = 0;
//		article = Article.NOTHING;
//	}
//}
