package grondag.facility.transport.buffer;

import net.minecraft.nbt.CompoundTag;

import grondag.facility.transport.handler.TransportCarrierContext;
import grondag.facility.transport.storage.TransportStorageContext;
import grondag.fluidity.api.article.Article;
import grondag.fluidity.api.transact.TransactionParticipant;
import grondag.fluidity.api.transact.TransactionParticipant.TransactionDelegate;

public abstract class TransportBuffer implements TransactionParticipant, TransactionDelegate {
	protected Article article = Article.NOTHING;

	protected Article rollBackArticle = Article.NOTHING;

	public abstract long accept(Article targetArticle, long numerator, long denominatorr, boolean simulate);

	public abstract long supply(Article targetArticle, long numerator, long denominatorr, boolean simulate);

	/** tries to empty buffer into storage */
	public abstract boolean clearBuffer(TransportStorageContext context);

	public abstract boolean clearBuffer(TransportCarrierContext carrierContext);

	public abstract CompoundTag toTag();

	public abstract void fromTag(CompoundTag tag);

	protected abstract Object createRollbackState();

	@Override
	public TransactionDelegate getTransactionDelegate() {
		return this;
	}

	public abstract boolean isEmpty();

	public abstract void reset();

}
