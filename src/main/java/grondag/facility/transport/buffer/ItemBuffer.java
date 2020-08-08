package grondag.facility.transport.buffer;

import java.util.function.Consumer;

import net.minecraft.nbt.CompoundTag;

import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.transact.TransactionContext;

public class ItemBuffer extends TransportBuffer {
	long quantity = 0;
	long rollbackQuantity = 0;

	@Override
	public long accept(Article article, long numerator, long divisor, boolean simulate) {
		if (quantity == 0 || article.equals(this.article)) {
			if (!simulate) {
				this.article = article;
				quantity += numerator;
			}

			return numerator;
		} else {
			return 0;
		}
	}

	@Override
	public long supply(Article article, long numerator, long divisor, boolean simulate) {
		if (quantity == 0 || !article.equals(this.article)) {
			return 0;
		} else {
			final long amt = Math.min(numerator, quantity);

			if (!simulate) {
				quantity -= amt;
			}

			return amt;
		}
	}

	@Override
	public boolean clearBuffer(TransportStorageContext context) {
		if (quantity == 0) {
			return true;
		} else {
			final long qty = context.accept(article, quantity, 1);
			assert qty <= quantity;
			assert qty >= 0;

			quantity -= qty;

			return quantity == 0;
		}
	}

	@Override
	public CompoundTag toTag() {
		final CompoundTag tag = new CompoundTag();
		tag.putLong("qty", quantity);
		tag.put("art", article.toTag());
		return tag;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		article = Article.fromTag(tag.get("art"));
		quantity = tag.getLong("qty");
	}

	protected final Consumer<TransactionContext> rollbackHandler = c -> {
		if (!c.isCommited()) {
			article = rollBackArticle;
			quantity = rollbackQuantity;
		}
	};

	@Override
	public Consumer<TransactionContext> prepareRollback(TransactionContext context) {
		rollBackArticle = article;
		rollbackQuantity = quantity;
		return rollbackHandler;
	}

	@Override
	protected Object createRollbackState() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return quantity == 0;
	}

	@Override
	public void reset() {
		quantity = 0;
		article = Article.NOTHING;
	}
}
