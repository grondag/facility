package grondag.smart_chest.delegate;

import java.util.Comparator;

import javax.annotation.Nullable;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;

import grondag.fermion.gui.container.ItemDisplayDelegate;

public class ItemDelegate implements ItemDisplayDelegate {
	public ItemStack stack;
	public long count;
	public int handle;

	public ItemDelegate(ItemStack stack, long count, int handle) {
		prepare(stack, count, handle);
	}

	public ItemDelegate prepare (ItemStack stack, long count, int handle) {
		this.stack = stack;
		this.count = count;
		this.handle = handle;
		return this;
	}

	@Override
	public ItemDelegate clone() {
		return new ItemDelegate(stack, count, handle);
	}

	@Override
	public int handle() {
		return handle;
	}

	@Override
	public ItemStack displayStack() {
		return stack;
	}

	@Override
	public long count() {
		return count;
	}

	/////////////////////////////////////////
	// SORTING UTILITIES
	/////////////////////////////////////////

	public static final Comparator<ItemDelegate> SORT_BY_NAME_ASC = new Comparator<ItemDelegate>() {
		@Override
		public int compare(@Nullable ItemDelegate o1, @Nullable ItemDelegate o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				}
				return 1;
			} else if (o2 == null) {
				return -1;
			}

			final String s1 = I18n.translate(o1.stack.getTranslationKey());
			final String s2 = I18n.translate(o2.stack.getTranslationKey());
			return s1.compareTo(s2);
		}
	};

	public static final Comparator<ItemDelegate> SORT_BY_NAME_DESC = new Comparator<ItemDelegate>() {
		@Override
		public int compare(@Nullable ItemDelegate o1, @Nullable ItemDelegate o2) {
			return SORT_BY_NAME_ASC.compare(o2, o1);
		}
	};

	public static final Comparator<ItemDelegate> SORT_BY_QTY_ASC = new Comparator<ItemDelegate>() {
		@Override
		public int compare(@Nullable ItemDelegate o1, @Nullable ItemDelegate o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				}
				return 1;
			} else if (o2 == null) {
				return -1;
			}
			final int result = Long.compare(o1.count, o2.count);
			return result == 0 ? SORT_BY_NAME_ASC.compare(o1, o2) : result;
		}
	};

	public static final Comparator<ItemDelegate> SORT_BY_QTY_DESC = new Comparator<ItemDelegate>() {
		@Override
		public int compare(@Nullable ItemDelegate o1, @Nullable ItemDelegate o2) {
			return SORT_BY_QTY_ASC.compare(o2, o1);
		}
	};

	public static final int SORT_COUNT = 4;
	public static final String[] SORT_LABELS = { "A-Z", "Z-A", "1-2-3", "3-2-1" };

	@SuppressWarnings("rawtypes")
	public static final Comparator[] SORT = { SORT_BY_NAME_ASC, SORT_BY_NAME_DESC, SORT_BY_QTY_ASC, SORT_BY_QTY_DESC };
}
