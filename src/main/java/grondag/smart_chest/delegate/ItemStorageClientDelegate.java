package grondag.smart_chest.delegate;

import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.client.resource.language.I18n;

public enum ItemStorageClientDelegate {
	;

	private static final Int2ObjectOpenHashMap<ItemDelegate> MAP = new Int2ObjectOpenHashMap<>();

	public static final ObjectArrayList<ItemDelegate> LIST = new ObjectArrayList<>();

	private static boolean isSortDirty = false;

	private static int sortIndex = 0;
	private static String filter = "";
	private static String lastFilter = "";

	private static long capacity;
	private static long usedCapacity;

	/**
	 * Incorporates changes and updates sort order. Returns true if a refresh was
	 * performed.
	 */

	public static boolean refreshListIfNeeded() {
		if (!isSortDirty) {
			return false;
		}

		@SuppressWarnings("unchecked")
		final Comparator<ItemDelegate> sort = ItemDelegate.SORT[sortIndex];
		LIST.sort(sort);
		isSortDirty = false;
		return true;
	}

	public static void handleStorageRefresh(List<ItemDelegate> update, long capacity, boolean isFullRefresh) {
		ItemStorageClientDelegate.capacity = capacity;

		if (isFullRefresh) {
			handleFullRefresh(update);
		} else if (!update.isEmpty()) {
			for (final ItemDelegate d : update) {
				handleDelegateUpdate(d);
			}
		}

		isSortDirty = true;
	}

	private static void handleFullRefresh(List<ItemDelegate> update) {
		MAP.clear();
		LIST.clear();
		usedCapacity = 0;

		for (final ItemDelegate item : update) {
			MAP.put(item.handle(), item);
			LIST.add(item);
			usedCapacity += item.count;
		}
	}

	private static void handleDelegateUpdate(ItemDelegate update) {
		final ItemDelegate prior = MAP.get(update.handle());

		if (prior == null) {
			MAP.put(update.handle, update);
			addToListIfIncluded(update);
			usedCapacity += update.count;
		} else if (update.count == 0) {
			MAP.remove(update.handle());
			LIST.remove(prior);
			usedCapacity -= prior.count;
		} else {
			usedCapacity += update.count - prior.count;
			prior.count = update.count;
			prior.stack = update.stack;
		}
	}

	public static void handleStorageDisconnect() {
		MAP.clear();
		LIST.clear();
		isSortDirty = false;
	}

	public static int getSortIndex() {
		return sortIndex;
	}

	public static void setSortIndex(int sortIndex) {
		ItemStorageClientDelegate.sortIndex = sortIndex;
		isSortDirty = true;
	}

	public static void setFilter(@Nullable String filter) {
		if (filter == null) {
			filter = "";
		}

		ItemStorageClientDelegate.filter = filter.toLowerCase();
		updateFilter();
	}

	private static void updateFilter() {
		if(!filter.equals(lastFilter)) {

			if(!lastFilter.equals("") && filter.startsWith(lastFilter)) {
				applyFilter();
			} else {
				LIST.clear();

				if(filter.equals("")) {
					LIST.addAll(MAP.values());
				} else {
					for(final ItemDelegate item : MAP.values()) {
						addToListIfIncluded(item);
					}
				}

				isSortDirty = true;
			}

			lastFilter = filter;
		}
	}

	private static void applyFilter() {
		for(int i = LIST.size() - 1; i >= 0; --i) {
			if(!I18n.translate(LIST.get(i).stack.getTranslationKey()).toLowerCase().contains(filter)) {
				LIST.remove(i);
			}
		}
	}

	private static void addToListIfIncluded(ItemDelegate delegate) {
		if(filter.equals("") || I18n.translate(delegate.stack.getTranslationKey()).toLowerCase().contains(filter)) {
			LIST.add(delegate);
		}
	}

	public static String getFilter() {
		return filter;
	}

	public static long capacity() {
		return capacity;
	}

	public static long usedCapacity() {
		return usedCapacity;
	}

	public static int fillPercentage() {
		return capacity == 0 ? 0 : (int) (usedCapacity * 100 / capacity);
	}
}
