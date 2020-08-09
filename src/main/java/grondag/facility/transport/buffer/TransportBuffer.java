package grondag.facility.transport.buffer;

import java.util.function.Consumer;

import net.minecraft.nbt.CompoundTag;

import grondag.facility.Facility;
import grondag.facility.transport.handler.TransportCarrierContext;
import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.article.ArticleType;
import grondag.fluidity.api.fraction.Fraction;
import grondag.fluidity.api.fraction.MutableFraction;
import grondag.fluidity.api.storage.ArticleFunction;
import grondag.fluidity.api.transact.Transaction;
import grondag.fluidity.api.transact.TransactionContext;
import grondag.fluidity.api.transact.TransactionParticipant;
import grondag.fluidity.api.transact.TransactionParticipant.TransactionDelegate;
import grondag.fluidity.wip.api.transport.CarrierNode;

public class TransportBuffer implements TransactionDelegate, TransactionParticipant {
	public class BufferState {
		private BufferState()  {}

		long itemQuantity = 0;
		Article itemArticle = Article.NOTHING;

		Article fluidArticle = Article.NOTHING;
		MutableFraction fluidAmount = new MutableFraction();

		public CompoundTag toTag() {
			final CompoundTag tag = new CompoundTag();

			if (itemQuantity != 0 && !itemArticle.isNothing()) {
				tag.put("itm", itemArticle.toTag());
				tag.putLong("itmqty", itemQuantity);
			}

			if (!fluidAmount.isZero() && !fluidArticle.isNothing()) {
				tag.put("fld", fluidArticle.toTag());
				tag.put("fldqty", fluidAmount.toTag());
			}

			return tag;
		}

		public void fromTag(CompoundTag tag) {
			reset();

			if (tag.contains("itm")) {
				itemArticle = Article.fromTag(tag.get("itm"));
				itemQuantity = tag.getLong("itmqty");
			}

			if (tag.contains("fld")) {
				fluidArticle = Article.fromTag(tag.get("fld"));
				fluidAmount.readTag(tag.getCompound("fldqty"));
			}
		}

		public boolean shouldSave() {
			return itemQuantity != 0 || !fluidAmount.isZero();
		}

		public void reset() {
			itemQuantity = 0;
			itemArticle = Article.NOTHING;
			fluidAmount.set(Fraction.ZERO);
			fluidArticle = Article.NOTHING;
		}

		private void copyFrom(BufferState other) {
			itemQuantity = other.itemQuantity;
			itemArticle = other.itemArticle;
			fluidAmount.set(other.fluidAmount);
			fluidArticle = other.fluidArticle;
		}
	}

	protected BufferState state = new BufferState();

	// we keep and reuse this to avoid allocation in most cases
	protected BufferState rollbackState = new BufferState();

	public BufferState state() {
		return state;
	}

	private final Consumer<TransactionContext> rollbackHandler = ctx -> {
		final BufferState rollbackState = ctx.getState();

		if (!ctx.isCommited()) {
			state.copyFrom(rollbackState);
		}

		// save instance for reuse
		this.rollbackState = rollbackState;
	};

	@Override
	public Consumer<TransactionContext> prepareRollback(TransactionContext context) {
		BufferState rollbackState = this.rollbackState;

		if (rollbackState == null) {
			// if nesting then will need to allocate
			rollbackState = new BufferState();
		} else {
			// no allocation - can reuse instance
			// setting instance to null signals it is in use
			this.rollbackState = null;
		}

		rollbackState.copyFrom(state);
		context.setState(rollbackState);
		return rollbackHandler;
	}

	public void flushFluidToStorage(TransportStorageContext storageContext) {
		final BufferState state = this.state;

		if (!state.fluidAmount.isZero()) {
			final MutableFraction amt = state.fluidAmount;
			final long div = amt.divisor();
			state.fluidAmount.subtract(storageContext.accept(state.fluidArticle, amt.numerator() + amt.whole() * div, div), div);

			assert !amt.isNegative();

			if (amt.isZero()) {
				state.fluidArticle = Article.NOTHING;
			}
		}
	}

	public void flushItemToStorage(TransportStorageContext storageContext) {
		final BufferState state = this.state;

		if (state.itemQuantity > 0) {
			final long howMany = storageContext.accept(state.itemArticle, state.itemQuantity, 1);
			state.itemQuantity -= howMany;

			// TODO: remove
			Facility.LOG.info(String.format("flushItemToStorage %d %s", howMany, state.itemArticle.toItem().getTranslationKey()));

			assert state.itemQuantity >= 0;

			if (state.itemQuantity == 0) {
				state.itemArticle = Article.NOTHING;
			}
		}
	}


	public void flushItemToCarrier(TransportCarrierContext carrierContext) {
		if(!carrierContext.isReady()) {
			return;
		}

		final BufferState state = this.state;

		if (state.itemQuantity == 0) {
			return;
		}

		final Article article = state.itemArticle;
		CarrierNode targetNode = carrierContext.lastTarget();

		if (!targetNode.isValid() || !targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get().canApply(article)) {
			targetNode = carrierContext.consumerFor(article);
		}

		if (!targetNode.isValid()) {
			return;
		}

		final ArticleFunction consumer = targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get();

		try (Transaction tx = Transaction.open()) {
			tx.enlist(consumer);
			tx.enlist(this);

			long howMany = carrierContext.throttle(article, state.itemQuantity, 1, false);
			howMany = consumer.apply(article, howMany, 1, false);

			assert howMany >= 0;
			assert howMany <= state.itemQuantity;

			if (howMany > 0) {
				state.itemQuantity -= howMany;
				tx.commit();
				carrierContext.resetCooldown();

				// TODO: remove
				Facility.LOG.info(String.format("flushItemToCarrier %d %s", howMany, article.toItem().getTranslationKey()));
			}
		}
	}

	public void flushFluidToCarrier(TransportCarrierContext carrierContext) {
		if(!carrierContext.isReady()) {
			return;
		}

		final BufferState state = this.state;
		final MutableFraction amt = state.fluidAmount;

		if (amt.isZero()) {
			return;
		}

		final Article article = state.fluidArticle;
		CarrierNode targetNode = carrierContext.lastTarget();

		if (!targetNode.isValid() || !targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get().canApply(article)) {
			targetNode = carrierContext.consumerFor(article);
		}

		if (!targetNode.isValid()) {
			return;
		}

		final ArticleFunction consumer = targetNode.getComponent(ArticleFunction.CONSUMER_COMPONENT).get();

		final long div = amt.divisor();
		long howMuch = amt.numerator() + amt.whole() * div;

		try (Transaction tx = Transaction.open()) {
			tx.enlist(consumer);
			tx.enlist(this);

			howMuch = carrierContext.throttle(article, howMuch, div, false);
			howMuch = consumer.apply(article, howMuch, div, false);

			assert howMuch >= 0;

			if (howMuch > 0) {
				amt.subtract(howMuch, div);
				tx.commit();
				carrierContext.resetCooldown();
			}
		}
	}

	private abstract class ArtFunc implements ArticleFunction {
		@Override
		public TransactionDelegate getTransactionDelegate() {
			return TransportBuffer.this;
		}

		@Override
		public long apply(Article article, long count, boolean simulate) {
			if (article.type().isItem()) {
				return applyItem(article, count, simulate);
			} else if (article.type().isFluid()) {
				return applyFluid(article, count, 1, simulate);
			} else {
				return 0;
			}
		}

		@Override
		public Fraction apply(Article article, Fraction volume, boolean simulate) {
			if (article.type().isItem()) {
				return Fraction.of(applyItem(article, volume.whole(), simulate));
			} else if (article.type().isFluid()) {
				if (volume.whole() > 0) {
					return Fraction.of(applyFluid(article, volume.whole(), 1, simulate));
				} else {
					return Fraction.of(applyFluid(article, volume.numerator(), volume.divisor(), simulate), volume.divisor());
				}
			} else {
				return Fraction.ZERO;
			}
		}

		@Override
		public long apply(Article article, long numerator, long divisor, boolean simulate) {
			if (article.type().isItem()) {
				return applyItem(article, numerator / divisor, simulate) * divisor;
			} else if (article.type().isFluid()) {
				return applyFluid(article, numerator, divisor, simulate);
			} else {
				return 0;
			}
		}

		@Override
		public Article suggestArticle(ArticleType<?> type) {
			return type == ArticleType.ITEM ? state.itemArticle : state.fluidArticle;
		}

		protected abstract long applyFluid(Article article, long numerator, long divisor, boolean simulate);

		protected abstract long applyItem(Article article, long qty, boolean simulate);
	}

	private final ArticleFunction consumer = new ArtFunc() {
		@Override
		protected long applyFluid(Article article, long numerator, long divisor, boolean simulate) {
			if (numerator == 0) {
				return 0;
			}

			if (!state.fluidAmount.isZero() && (!state.fluidArticle.equals(article))) {
				return 0;
			}

			final long capacity = divisor - state.fluidAmount.toLong(divisor);

			if (capacity <= 0) {
				return 0;
			}

			final long result = Math.min(numerator, capacity);

			if (!simulate) {
				state.fluidAmount.add(result, divisor);
				state.fluidArticle = article;
			}

			return result;
		}

		@Override
		protected long applyItem(Article article, long qty, boolean simulate) {
			if (qty  == 0) {
				return 0;
			}

			final long maxCount = article.toItem().getMaxCount();

			if (state.itemQuantity != 0 && !state.itemArticle.equals(article)) {
				return 0;
			}

			final long capacity = maxCount - state.itemQuantity;

			if (capacity <= 0) {
				return 0;
			}

			final long result = Math.min(qty, capacity);

			if (!simulate) {
				state.itemQuantity += result;
				state.itemArticle = article;
			}

			return result;
		}
	};

	public ArticleFunction consumer() {
		return consumer;
	}

	private final ArticleFunction supplier = new ArtFunc() {
		@Override
		protected long applyFluid(Article article, long numerator, long divisor, boolean simulate) {
			if (numerator == 0 || state.fluidAmount.isZero() || state.fluidArticle.isNothing() || !state.fluidArticle.equals(article)) {
				return 0;
			}

			final long avail = state.fluidAmount.toLong(divisor);
			final long result = Math.min(numerator, avail);

			if (!simulate) {
				state.fluidAmount.subtract(result, divisor);

				if (state.fluidAmount.isZero()) {
					state.fluidArticle = Article.NOTHING;
				}
			}

			return result;
		}

		@Override
		protected long applyItem(Article article, long qty, boolean simulate) {
			if (qty  == 0 || state.itemQuantity == 0 || state.itemArticle.isNothing() || !state.itemArticle.equals(article)) {
				return 0;
			}

			final long result = Math.min(qty, state.itemQuantity);

			if (!simulate) {
				state.itemQuantity -= result;

				if (state.itemQuantity == 0) {
					state.itemArticle = Article.NOTHING;
				}
			}

			return result;
		}
	};

	public ArticleFunction supplier() {
		return supplier;
	}

	@Override
	public TransactionDelegate getTransactionDelegate() {
		return this;
	}
}
