//package grondag.facility.wip.transport.impl;
//
//import java.util.Iterator;
//import java.util.function.Consumer;
//
//import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
//import it.unimi.dsi.fastutil.objects.ObjectArrayList;
//
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.item.Item;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//
//import grondag.facility.block.ItemStorageBlockEntity.ItemStorageMultiblock;
//import grondag.facility.block.PipeBlockEntity.PipeMultiblock;
//import grondag.facility.wip.transport.CarrierDevice;
//import grondag.facility.wip.transport.CarrierNode;
//import grondag.facility.wip.transport.NodeDevice;
//import grondag.facility.wip.transport.StorageConnection;
//import grondag.fluidity.api.article.Article;
//import grondag.fluidity.api.device.CompoundDeviceManager;
//import grondag.fluidity.api.device.CompoundMemberDevice;
//import grondag.fluidity.api.device.Device;
//import grondag.fluidity.api.storage.ArticleConsumer;
//import grondag.fluidity.api.storage.ArticleSupplier;
//import grondag.fluidity.api.transact.TransactionContext;
//import grondag.fluidity.api.transact.TransactionParticipant.TransactionDelegate;
//import grondag.fluidity.base.device.AbstractCompoundDevice;
//import grondag.fluidity.base.storage.discrete.DiscreteStorage.DiscreteArticleConsumer;
//import grondag.fluidity.base.storage.discrete.DiscreteStorage.DiscreteArticleSupplier;
//
//public abstract class AbstractCompoundCarrierDevice<T extends CompoundMemberDevice<T, U>, U extends AbstractCompoundCarrierDevice<T, U>> extends AbstractCompoundDevice<T, U> implements CarrierDevice {
//
//	final Long2ObjectOpenHashMap<Node> topNodes = new Long2ObjectOpenHashMap<>();
//
//	Item i;
//	@Override
//	protected void onRemove(PipeBlockEntity device) {
//		for(final Node n : device.nodes) {
//			topNodes.remove(n.address);
//		}
//	}
//
//	@Override
//	protected void onAdd(PipeBlockEntity device) {
//		for(final Node n : device.nodes) {
//			topNodes.put(n.address, n);
//		}
//	}
//}
//
//protected static final CompoundDeviceManager<ItemStorageBlockEntity, ItemStorageMultiblock> DEVICE_MANAGER = CompoundDeviceManager.create(
//		ItemStorageMultiblock::new, (ItemStorageBlockEntity a, ItemStorageBlockEntity b) -> ItemStorageBlock.canConnect(a, b));
//
//protected PipeMultiblock owner = null;
//final ObjectArrayList<Node> nodes = new ObjectArrayList<>();
//
//public PipeBlockEntity(BlockEntityType<PipeBlockEntity> type) {
//	super(type);
//}
//
//@Override
//protected void addNeighbor(BlockEntity be, BlockPos neighborPos, Direction neighborSide) {
//	if(!(be instanceof PipeBlockEntity) && be instanceof CarrierDevice) {
//		neighbors.add((NodeDevice) be);
//
//		((NodeDevice) be).onCarrierPresent(this);
//	}
//}
//
//@Override
//public void onCarrierPresent(CarrierDevice carrierDevice) {
//	if(carrierDevice.isGateway()) {
//		// TODO Auto-generated method stub
//	}
//}
//
//@Override
//public CarrierNode attach(Device fromDevice, Direction fromDirection, ArticleConsumer broadcastConsumer, ArticleSupplier broadcastSupplier) {
//	return new Node(broadcastConsumer, broadcastSupplier);
//}
//
//@Override
//public PipeMultiblock getCompoundDevice() {
//	return owner;
//}
//
//@Override
//public void setCompoundDevice(PipeMultiblock owner) {
//	this.owner = owner;
//}
//
////TODO: make and use ANA
//static long nextAddress = 0;
//
//private class Node implements CarrierNode, DiscreteArticleConsumer, DiscreteArticleSupplier, TransactionDelegate {
//	final long address = ++nextAddress;
//	final ArticleConsumer broadcastConsumer;
//	final ArticleSupplier broadcastSupplier;
//	protected boolean isOpen = true;
//
//	Node(ArticleConsumer broadcastConsumer, ArticleSupplier broadcastSupplier) {
//		this.broadcastConsumer = broadcastConsumer;
//		this.broadcastSupplier = broadcastSupplier;
//		nodes.add(this);
//
//		if(owner != null ) {
//			owner.topNodes.put(address, this);
//		}
//	}
//
//	@Override
//	public long address() {
//		return address;
//	}
//
//	@Override
//	public boolean isValid() {
//		return isOpen && !isRemoved();
//	}
//
//	@Override
//	public ArticleConsumer broadcastConsumer() {
//		return this;
//	}
//
//	@Override
//	public ArticleSupplier broadcastSupplier() {
//		return this;
//	}
//
//	@Override
//	public StorageConnection connect(long remoteAddress) {
//		return null;
//	}
//
//	@Override
//	public void close() {
//		if(isOpen) {
//			isOpen = false;
//			nodes.remove(this);
//
//			if(owner != null ) {
//				owner.topNodes.remove(address);
//			}
//		}
//	}
//
//	Iterator<Node> getNodes() {
//		return owner ==  null ? nodes.iterator() : owner.topNodes.values().iterator();
//	}
//
//	@Override
//	public long supply(Article item, long count, boolean simulate) {
//		if(nodes.isEmpty()) {
//			return 0;
//		}
//
//		long result = 0;
//
//		final Iterator<Node> it = getNodes();
//
//		while(it.hasNext()) {
//			final Node n = it.next();
//
//			if(n.broadcastSupplier != null) {
//				result += n.broadcastSupplier.supply(item, count - result, simulate);
//
//				if(result >= count) {
//					break;
//				}
//			}
//		}
//
//		return result;
//	}
//
//	@Override
//	public long accept(Article item, long count, boolean simulate) {
//		if(nodes.isEmpty()) {
//			return 0;
//		}
//
//		long result = 0;
//
//		final Iterator<Node> it = nodes.iterator();
//
//		while(it.hasNext()) {
//			final Node n = it.next();
//
//			if(n.broadcastConsumer != null) {
//				result += n.broadcastConsumer.accept(item, count - result, simulate);
//
//				if(result >= count) {
//					break;
//				}
//			}
//		}
//
//		return result;
//	}
//
//	@Override
//	public TransactionDelegate getTransactionDelegate() {
//		return this;
//	}
//
//	@Override
//	public Consumer<TransactionContext> prepareRollback(TransactionContext context) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}
//}
