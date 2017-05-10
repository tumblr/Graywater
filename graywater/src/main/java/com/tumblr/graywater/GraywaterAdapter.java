package com.tumblr.graywater;

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * Maps models to multiple view holders.
 * <p>
 * Created by ericleong on 3/11/16.
 *
 * @param <T>
 * 		the model type.
 * @param <VH>
 * 		the viewholder type.
 * @param <MT>
 * 		the type of the model type ({@code Class<T>} for example)
 */
public abstract class GraywaterAdapter<T, VH extends RecyclerView.ViewHolder, MT> extends RecyclerView.Adapter<VH> {

	private static final int NO_PREVIOUS_BOUND_VIEWHOLDER = -1;

	/**
	 * The T list for adapter items.
	 */
	@NonNull
	protected final List<T> mItems = new ArrayList<>();

	/**
	 * Set of available binders. This is used to generate the ViewTypes.
	 */
	@NonNull
	private final Map<Class<? extends VH>, ViewHolderCreator> mViewHolderCreatorMap = new ArrayMap<>();

	/**
	 * Set of available binders. This is used to generate the ViewTypes.
	 */
	@NonNull
	private final SparseArray<Class<? extends VH>> mViewHolderCreatorList = new SparseArray<>();

	/**
	 * Map from model to a list of binders. A model may have multiple binders.
	 */
	@NonNull
	protected final Map<MT, ItemBinder<? extends T, ? extends VH>> mItemBinderMap = new ArrayMap<>();

	/**
	 * Map from model to an action listener. A model may have multiple action listeners if there are multiple events.
	 */
	@NonNull
	private final Map<MT, ActionListener<? extends T, ? extends VH>> mActionListenerMap
			= new ArrayMap<>();

	/**
	 * Index of the last model that was bound.
	 */
	private int mPreviousBoundViewHolderPosition = NO_PREVIOUS_BOUND_VIEWHOLDER;

	private final List<List<Binder<? super T, ? extends VH>>> mBinderListCache = new ArrayList<>();
	private final List<Integer> mViewHolderToItemPositionCache = new ArrayList<>();
	private final List<Integer> mItemPositionToFirstViewHolderPositionCache = new ArrayList<>();
	/**
	 * The viewholders that have had {@link #prepare(int, Binder, Object, List, int)} called on them.
	 *
	 * {@link #add(int, Object)}, {@link #remove(int)}, {@link #onViewRecycled(RecyclerView.ViewHolder)}
	 * will all cause this to be cleared.
	 */
	private final Set<Integer> mViewHolderPreparedCache = new HashSet<>();

	/**
	 * @param viewHolderCreator
	 * 		the view holder creator to register.
	 * @param viewHolderClass
	 * 		the class of the viewholder.
	 */
	protected void register(final ViewHolderCreator viewHolderCreator, final Class<? extends VH> viewHolderClass) {
		final ViewHolderCreator old = mViewHolderCreatorMap.put(viewHolderClass, viewHolderCreator);
		if (old != null) {
			mViewHolderCreatorList.delete(old.getViewType());
		}
		mViewHolderCreatorList.put(viewHolderCreator.getViewType(), viewHolderClass);
	}

	/**
	 * @param modelType
	 * 		the model type.
	 * @param parts
	 * 		the binders to use to display this model.
	 * @param listener
	 * 		the listener to associate with the model.
	 */
	protected void register(@NonNull final MT modelType,
	                        @NonNull final ItemBinder<? extends T, ? extends VH> parts,
	                        @Nullable final ActionListener<? extends T, ? extends VH> listener) {
		mItemBinderMap.put(modelType, parts);
		mActionListenerMap.put(modelType, listener);
	}

	/**
	 * @param model
	 * 		the model to get the type of.
	 * @return the appropriate type (for example, {@link Class<T>}).
	 */
	protected abstract MT getModelType(T model);

	/**
	 * @param model
	 * 		the model to get parts for.
	 * @param position
	 * 		the position of the model.
	 * @return the list of binders to use.
	 */
	@Nullable
	protected List<Binder<? super T, ? extends VH>> getParts(final T model, final int position) {
		final List<Binder<? super T, ? extends VH>> list;

		final ItemBinder itemBinder = mItemBinderMap.get(getModelType(model));

		if (itemBinder != null) {
			list = itemBinder.getBinderList(model, position);

			for (final Binder<? super T, ? extends VH> binder : list) {
				if (!mViewHolderCreatorMap.containsKey(binder.getViewHolderType())) {
					throw new IllegalArgumentException("Need to register "
							+ binder.getViewHolderType().getCanonicalName()
							+ " before adding a ItemBinder that uses it.");
				}
			}
		} else {
			list = null;
		}

		return list;
	}

	/**
	 * Computes the position of the item and the position of the binder within the item's binder list.
	 * Note that this is an <i>O(n)</i> operation.
	 *
	 * @param viewHolderPosition
	 * 		the position of the view holder in the adapter.
	 * @return the item position and the position of the binder in the item's binder list.
	 */
	@VisibleForTesting
	BinderResult computeItemAndBinderIndex(final int viewHolderPosition) {
		// subtract off the length of each list until we get to the desired item

		final int itemIndex = mViewHolderToItemPositionCache.get(viewHolderPosition);
		final T item = mItems.get(itemIndex);
		final List<Binder<? super T, ? extends VH>> binders = mBinderListCache.get(itemIndex);
		// index of the first item in the set of viewholders for the current item.
		final int firstVHPosForItem = mItemPositionToFirstViewHolderPositionCache.get(itemIndex);

		return new BinderResult(item, itemIndex, binders, viewHolderPosition - firstVHPosForItem);
	}

	@Override
	public int getItemViewType(final int position) {

		final BinderResult result = computeItemAndBinderIndex(position);

		final Binder<? super T, ? extends VH> binder = result.getBinder();
		final int viewType;

		if (binder != null) {
			viewType = mViewHolderCreatorMap.get(binder.getViewHolderType()).getViewType();
		} else {
			viewType = -1;
		}

		return viewType;
	}

	/**
	 * @param viewType
	 * 		the internal viewtype.
	 * @return the viewholder class.
	 */
	protected Class<? extends VH> getViewHolderClass(final int viewType) {
		return mViewHolderCreatorList.get(viewType);
	}

	@Override
	public VH onCreateViewHolder(final ViewGroup parent, final int viewType) {
		return (VH) mViewHolderCreatorMap.get(getViewHolderClass(viewType)).create(parent);
	}

	@Override
	@SuppressLint("RecyclerView")
	public void onBindViewHolder(final VH holder, final int viewHolderPosition) {

		final BinderResult result = computeItemAndBinderIndex(viewHolderPosition);
		final Binder binder = result.getBinder();

		if (binder != null && result.item != null) {

			if (mPreviousBoundViewHolderPosition == NO_PREVIOUS_BOUND_VIEWHOLDER) {
				prepare(viewHolderPosition, binder, result.item, result.binderList, result.binderIndex);
			}

			final ActionListener actionListener = mActionListenerMap.get(getModelType(result.item));

			binder.bind(result.item, holder, result.binderList, result.binderIndex, actionListener);

			prepareInternal(viewHolderPosition);
			mPreviousBoundViewHolderPosition = viewHolderPosition;
		}
	}

	private void prepareInternal(final int viewHolderPosition) {
		prepare(viewHolderPosition, Integer.signum(viewHolderPosition - mPreviousBoundViewHolderPosition));
	}

	/**
	 * Calls {@link #prepare(int, Binder, Object, List, int)}.
	 *
	 * @param lastBoundViewHolderPosition
	 * 		the position of the last viewholder that was bound.
	 * @param direction
	 * 		the direction the list is moving.
	 */
	protected void prepare(final int lastBoundViewHolderPosition, final int direction) {
		for (int i = 1; i <= numViewHoldersToPrepare(); i++) {
			final int viewHolderPosition = lastBoundViewHolderPosition + direction * i;
			if (isViewHolderPositionWithinBounds(viewHolderPosition)) {
				final BinderResult result = computeItemAndBinderIndex(viewHolderPosition);
				final Binder binder = result.getBinder();

				if (binder != null && result.item != null) {
					prepare(viewHolderPosition, binder, result.item, result.binderList, result.binderIndex);
				}
			}
		}
	}

	/**
	 * Calls {@link Binder#prepare(Object, List, int)}.

	 * @param viewHolderPosition
	 *      the position of the viewholder.
	 * @param binder
	 *      the binder to call.
	 * @param model
	 *      the model being prepared.
	 * @param binderList
	 * 		the list of binders
	 * @param binderIndex
	 * 		the index in the list of viewholders associated with this model
	 */
	protected void prepare(final int viewHolderPosition,
	                       final Binder<T, VH> binder,
	                       final T model,
	                       final List<Binder<? super T, ? extends VH>> binderList,
	                       final int binderIndex) {
		if (!mViewHolderPreparedCache.contains(viewHolderPosition)) {
			binder.prepare(model, binderList, binderIndex);
			mViewHolderPreparedCache.add(viewHolderPosition);
		}
	}

	/**
	 * @return Number of viewholders to prepare ahead.
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	protected int numViewHoldersToPrepare() {
		return 3;
	}

	/**
	 *  Checks if the model position is within the bounds of the underlying List
	 *
	 * @param itemPosition
	 *      model item position.
	 * @return true if within list bounds. False otherwise.
	 */
	protected boolean isItemPositionWithinBounds(final int itemPosition) {
		return itemPosition >= 0 && itemPosition < mItems.size();
	}

	/**
	 * Checks if the viewholder is within the bounds of underlying list.
	 * 
	 * @param viewHolderPosition
	 *      viewholder position
	 * @return true of within list bound. False otherwise.
	 */
	protected boolean isViewHolderPositionWithinBounds(final int viewHolderPosition) {
		return viewHolderPosition >= 0 && viewHolderPosition < getItemCount();
	}

	/**
	 * Note that this is an <i>O(n)</i> operation, but it does not query for the list of binders.
	 *
	 * @param itemPosition
	 * 		the position in the list of items.
	 * @return the number of viewholders before the given item position.
	 */
	@VisibleForTesting
	public int getViewHolderCount(final int itemPosition) {
		if (itemPosition >= 0 && !mItemPositionToFirstViewHolderPositionCache.isEmpty()) {
			if (itemPosition >= mItemPositionToFirstViewHolderPositionCache.size()) {
				return mViewHolderToItemPositionCache.size();
			} else {
				return mItemPositionToFirstViewHolderPositionCache.get(itemPosition);
			}
		} else {
			return 0;
		}
	}

	@Override
	public int getItemCount() {
		return mViewHolderToItemPositionCache.size();
	}

	/**
	 * @param item
	 * 		the item to add to the adapter.
	 */
	public void add(@NonNull final T item) {
		add(mItems.size(), item);
	}

	/**
	 * @param item
	 * 		the item to add to the adapter.
	 * @param notify
	 * 		whether or not to notify the adapter.
	 */
	public void add(@NonNull final T item, final boolean notify) {
		add(mItems.size(), item, notify);
	}

	/**
	 * This is an <i>O(1)</i> operation since it is cached.
	 *
	 * @param viewHolderPosition
	 * 		the position in the view holder.
	 * @return the position of the item in the list of items.
	 */
	public int getItemPosition(final int viewHolderPosition) {
		return mViewHolderToItemPositionCache.get(viewHolderPosition);
	}

	/**
	 *
	 * @param itemIndex
	 *      the current view holders binder position.
	 * @param viewHolderPosition
	 *      the view holder position.
	 * @return the binder index associated with view holder.
	 */
	public int getBinderPosition(final int itemIndex, final int viewHolderPosition) {
		return viewHolderPosition - mItemPositionToFirstViewHolderPositionCache.get(itemIndex);
	}

	/**
	 * Note that this is an <i>O(n)</i> operation, since the cache needs to be updated.
	 *
	 * @param position
	 * 		the position to insert into the list.
	 * @param item
	 * 		the item to add. Note that if it is <code>null</code>, there is no way to determine which binder to use.
	 */
	public void add(final int position, @NonNull final T item) {
		add(position, item, true);
	}

	/**
	 * Note that this is an <i>O(n)</i> operation, since the cache needs to be updated.
	 *
	 * @param position
	 * 		the position to insert into the list.
	 * @param item
	 * 		the item to add. Note that if it is <code>null</code>, there is no way to determine which binder to use.
	 * @param notify
	 * 		whether or not to notify the adapter.
	 */
	public void add(final int position, @NonNull final T item, final boolean notify) {
		final int numViewHolders = getViewHolderCount(position);

		final List<Binder<? super T, ? extends VH>> binders = getParts(item, position);

		mItems.add(position, item);
		mBinderListCache.add(position, binders);

		if (binders != null) {
			if (notify) {
				notifyItemRangeInserted(numViewHolders, binders.size());
			}

			final List<Integer> itemPositions = new ArrayList<>();
			for (int i = 0; i < binders.size(); i++) {
				itemPositions.add(position);
			}

			mViewHolderToItemPositionCache.addAll(numViewHolders, itemPositions);
			for (int viewHolderIndex = numViewHolders + binders.size(); viewHolderIndex < mViewHolderToItemPositionCache.size();
			     viewHolderIndex++) {
				mViewHolderToItemPositionCache.set(viewHolderIndex, mViewHolderToItemPositionCache.get(viewHolderIndex) + 1);
				mViewHolderPreparedCache.remove(viewHolderIndex);
			}

			mItemPositionToFirstViewHolderPositionCache.add(position, numViewHolders);
			for (int itemIndex = position + 1; itemIndex < mItemPositionToFirstViewHolderPositionCache.size(); itemIndex++) {
				mItemPositionToFirstViewHolderPositionCache.set(itemIndex,
						mItemPositionToFirstViewHolderPositionCache.get(itemIndex) + binders.size());
			}
		}
	}

	/**
	 * Note that this is an <i>O(n)</i> operation, since the cache needs to be updated.
	 *
	 * @param itemPosition
	 * 		removes the item at the position from the adapter.
	 * @return the removed item, or <code>null</code> if the position was out of bounds.
	 */
	@Nullable
	public T remove(final int itemPosition) {

		final T item;

		if (isItemPositionWithinBounds(itemPosition)) {
			final int numViewHolders = getViewHolderCount(itemPosition);

			item = mItems.get(itemPosition);

			final List<? extends Binder<? super T, ? extends VH>> binders = mBinderListCache.get(itemPosition);

			mItems.remove(itemPosition);

			for (final ListIterator<Integer> iter = mViewHolderToItemPositionCache.listIterator(); iter.hasNext();) {
				if (iter.next() == itemPosition) {
					iter.remove();
				}
			}

			for (int viewHolderIndex = numViewHolders; viewHolderIndex < mViewHolderToItemPositionCache.size();
			     viewHolderIndex++) {
				mViewHolderToItemPositionCache.set(viewHolderIndex, mViewHolderToItemPositionCache.get(viewHolderIndex) - 1);
				mViewHolderPreparedCache.remove(viewHolderIndex);
			}

			mItemPositionToFirstViewHolderPositionCache.remove(itemPosition);

			for (int itemIndex = itemPosition; itemIndex < mItemPositionToFirstViewHolderPositionCache.size(); itemIndex++) {
				mItemPositionToFirstViewHolderPositionCache.set(itemIndex,
						mItemPositionToFirstViewHolderPositionCache.get(itemIndex) - binders.size());
			}

			mBinderListCache.remove(itemPosition);

			if (binders != null) {
				notifyItemRangeRemoved(numViewHolders, binders.size());
			}
		} else {
			item = null;
		}

		return item;
	}

	/**
	 * Calls {@link #notifyItemRangeChanged(int, int)} on the viewholders associated with the item.
	 *
	 * @param itemPosition
	 * 		the item position that was updated.
	 * @return whether or not {@link #notifyItemRangeChanged(int, int)} was called.
	 */
	public boolean updateItem(final int itemPosition) {
		boolean notified = false;

		if (isItemPositionWithinBounds(itemPosition)) {
			final int numViewHolders = getViewHolderCount(itemPosition);

			final T item = mItems.get(itemPosition);

			final List<Binder<? super T, ? extends VH>> binders = getParts(item, itemPosition);
			if (binders == null) {
				return false;
			}
			final int newBinderLength = binders.size();
			final int oldBinderLength = mBinderListCache.get(itemPosition).size();

			mBinderListCache.set(itemPosition, binders);

			final int diff = Math.abs(oldBinderLength - newBinderLength);
			// Using the difference in binder length, remove or add to the range.
			for (int i = 0; i < diff; i++) {
				if (oldBinderLength < newBinderLength) {
					mViewHolderToItemPositionCache.add(numViewHolders, itemPosition);
				} else {
					mViewHolderToItemPositionCache.remove(numViewHolders);
				}
			}

			// We need to update the pointers to the first viewholders.
			for (int itemIndex = itemPosition + 1; itemIndex < mItemPositionToFirstViewHolderPositionCache.size(); itemIndex++) {
				mItemPositionToFirstViewHolderPositionCache.set(itemIndex,
						mItemPositionToFirstViewHolderPositionCache.get(itemIndex) + (newBinderLength - 1));
			}

			notifyItemRangeChanged(numViewHolders, binders.size());
			notified = true;
		}

		return notified;
	}

	/**
	 * Finds the adapter data position for the first instance of a particular view holder used in an item.
	 *
	 * @param itemPosition
	 * 		the position for the item that uses the view holder.
	 * @param viewHolderClass
	 *      the view holder type to look for.
	 * @return the adapter data position for the view holder, or -1 if not found.
	 */
	public int getFirstViewHolderPosition(final int itemPosition, @NonNull final Class viewHolderClass) {
		if (isItemPositionWithinBounds(itemPosition)) {
			final int itemStartPos = getViewHolderCount(itemPosition);
			int viewHolderIndex = 0;
			final List<Binder<? super T, ? extends VH>> binders = mBinderListCache.get(itemPosition);
			for (Binder<? super T, ? extends VH> binder : binders) {
				if (binder.getViewHolderType().equals(viewHolderClass)) {
					return itemStartPos + viewHolderIndex;
				}
				viewHolderIndex++;
			}
		}
		return -1;
	}

	/**
	 * Clears the adapter.
	 */
	public void clear() {
		mItems.clear();
		mBinderListCache.clear();
		mViewHolderToItemPositionCache.clear();
		mItemPositionToFirstViewHolderPositionCache.clear();
		mViewHolderPreparedCache.clear();
		mPreviousBoundViewHolderPosition = NO_PREVIOUS_BOUND_VIEWHOLDER;
	}

	/**
	 * This is very similar to {@link View#inflate(android.content.Context, int, ViewGroup)}
	 * but does not attach the inflated view.
	 *
	 * @param parent
	 * 		the parent viewgroup
	 * @param layoutRes
	 * 		the layout to inflate
	 * @return the inflated and unattached view.
	 */
	public static View inflate(final ViewGroup parent, @LayoutRes final int layoutRes) {
		return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
	}

	@Override
	public void onViewRecycled(final VH holder) {
		super.onViewRecycled(holder);

		final int viewHolderPosition = holder.getAdapterPosition();

		if (isViewHolderPositionWithinBounds(viewHolderPosition)) {
			final BinderResult result = computeItemAndBinderIndex(viewHolderPosition);
			final Binder binder = result.getBinder();

			if (binder != null) {
				mViewHolderPreparedCache.remove(viewHolderPosition);
				binder.unbind(holder);
			}

			// N.B. this fails when remove() is called (note that the item is gone)
		}
	}

	@NonNull
	public List<T> getItems() {
		return mItems;
	}

	/**
	 * @param itemPosition
	 * 		the item position.
	 * @return the binders that belong to the item at the given position.
	 */
	@Nullable
	public List<Binder<? super T, ? extends VH>> getBindersForPosition(final int itemPosition) {
		final List<Binder<? super T, ? extends VH>> binders;

		if (isItemPositionWithinBounds(itemPosition)) {
			binders = mBinderListCache.get(itemPosition);
		} else {
			binders = null;
		}

		return binders;
	}

	/**
	 * @param itemPosition
	 * 		the item position.
	 * @return the range of viewholders that represent the item. The first is the offset, the second is the count.
	 */
	@Nullable
	public Pair<Integer, Integer> getViewHolderRange(final int itemPosition) {
		final Pair<Integer, Integer> range;

		if (isItemPositionWithinBounds(itemPosition)) {
			final int numViewHolders = getViewHolderCount(itemPosition);

			final List<Binder<? super T, ? extends VH>> binders = mBinderListCache.get(itemPosition);

			range = new Pair<>(numViewHolders, binders.size());
		} else {
			range = null;
		}

		return range;
	}

	/**
	 * Binds a model of type {@code U} to a viewholder of type {@code V}.
	 *
	 * @param <U>
	 * 		the model.
	 * @param <V>
	 * 		the viewholder.
	 */
	public interface Binder<U, V extends RecyclerView.ViewHolder> {

		/**
		 * @return the type of the viewholder.
		 */
		@NonNull
		Class<V> getViewHolderType();

		/**
		 * Called to notify this binder that it may be called soon in the future. It may be called multiple times
		 * before the view is actually ready to be bound. It is useful for preloading images.
		 *
		 * @param model
		 * 		the model that will be bound.
		 * @param binderList
		 * 		the list of binders.
		 * @param binderIndex
		 * 		the index of the binder in the list of binders.
		 */
		void prepare(@NonNull U model, List<Binder<? super U, ? extends V>> binderList, int binderIndex);

		/**
		 * Called when {@link android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder,
		 * int)}
		 * is called.
		 *
		 * @param model
		 * 		the model to bind to the viewholder
		 * @param holder
		 * 		the viewholder to update
		 * @param binderList
		 * 		the list of binders
		 * @param binderIndex
		 * 		the index in the list of viewholders associated with this model
		 * @param actionListener
		 * 		the action listener to use
		 */
		void bind(@NonNull U model, @NonNull V holder, @NonNull List<Binder<? super U, ? extends V>> binderList,
		          int binderIndex, @NonNull ActionListener<U, V> actionListener);

		/**
		 * Called when {@link android.support.v7.widget.RecyclerView.Adapter#onViewRecycled(RecyclerView.ViewHolder)}
		 * is called.
		 *
		 * @param holder
		 * 		the view holder that was recycled.
		 */
		void unbind(@NonNull V holder);
	}

	/**
	 * Creates a viewholder.
	 */
	public interface ViewHolderCreator {
		/**
		 * Called when {@link android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
		 * is called.
		 *
		 * @param parent
		 * 		the parent view.
		 * @return the inflated view.
		 */
		RecyclerView.ViewHolder create(ViewGroup parent);

		/**
		 * In nearly all cases, this should simply return the layout id.
		 *
		 * @return the view type to associate with this {@link android.support.v7.widget.RecyclerView.ViewHolder}.
		 */
		int getViewType();
	}

	/**
	 * Gets the list of binders associated with an item.
	 *
	 * @param <U>
	 * 		the model type.
	 * @param <V>
	 * 		the viewholder type.
	 */
	public interface ItemBinder<U, V extends RecyclerView.ViewHolder> {
		/**
		 * @param model
		 * 		the model that will be bound.
		 * @param position
		 * 		the position of the model in the list.
		 * @return the list of binders to use.
		 */
		@NonNull
		List<Binder<? super U, ? extends V>> getBinderList(@NonNull U model, int position);
	}

	/**
	 * @param <U>
	 * 		the model type.
	 * @param <V>
	 * 		the viewholder type.
	 */
	public interface ActionListener<U, V extends RecyclerView.ViewHolder> {
		/**
		 * @param model
		 * 		the model associated with the view that was modified.
		 * @param holder
		 * 		the viewholder associated with the view that was touched.
		 * @param v
		 * 		the view that was touched.
		 * @param binderList
		 * 		the list of binders associated with the model.
		 * @param binderIndex
		 * 		the index of the binder that was modified.
		 * @param obj
		 * 		an extra object for message passing.
		 */
		void act(@NonNull U model, @NonNull V holder, @NonNull View v,
		         @NonNull List<Binder<? super U, ? extends V>> binderList,
		         int binderIndex, @Nullable Object obj);
	}

	/**
	 * A helper {@link android.view.View.OnClickListener} that can be used to hold references to objects that are
	 * passed in during a {@link Binder#bind(Object, RecyclerView.ViewHolder, List, int, ActionListener)}.
	 * <p>
	 * Note that it uses strong references.
	 *
	 * @param <U>
	 * 		the model type.
	 * @param <V>
	 * 		the viewholder type.
	 */
	public static class ActionListenerDelegate<U, V extends RecyclerView.ViewHolder>
			implements View.OnClickListener {
		/**
		 * The model.
		 */
		public U model;
		/**
		 * The viewholder.
		 */
		public V holder;
		/**
		 * The list of binders.
		 */
		public List<Binder<? super U, ? extends V>> binders;
		/**
		 * The index into the list of binders.
		 */
		public int binderIndex;
		/**
		 * A spare object to pass around.
		 */
		@Nullable
		public Object obj;
		/**
		 * The listener to call on click.
		 */
		public ActionListener<U, V> actionListener;

		/**
		 * @param actionListener
		 * 		the listener to call on click.
		 * @param model
		 * 		the model that is being clicked.
		 * @param holder
		 * 		the view holder that is being clicked.
		 * @param binders
		 * 		the list of binders associated with the model.
		 * @param binderIndex
		 * 		the index into the list of binders of the view holder that is being clicked.
		 * @param obj
		 * 		an extra object that can be used to pass around extra data.
		 */
		public void update(final ActionListener<U, V> actionListener,
		                   @NonNull final U model, @NonNull final V holder,
		                   @NonNull final List<Binder<? super U, ? extends V>> binders, final int binderIndex,
		                   @Nullable final Object obj) {
			this.model = model;
			this.holder = holder;
			this.binders = binders;
			this.binderIndex = binderIndex;
			this.obj = obj;
			this.actionListener = actionListener;
		}

		@Override
		public void onClick(final View v) {
			actionListener.act(model, holder, v, binders, binderIndex, obj);
		}
	}

	/**
	 * Internal class that holds the item and binder associated with a viewholder.
	 */
	@VisibleForTesting
	final class BinderResult {
		/**
		 * The item associated with the viewholder.
		 */
		@Nullable
		public final T item;
		/**
		 * The position of th item in the list of items.
		 */
		@VisibleForTesting
		public final int itemPosition;
		/**
		 * The list of binders associated with the item.
		 */
		@Nullable
		public final List<Binder<? super T, ? extends VH>> binderList;
		/**
		 * The index of the binder to use in the list of binders.
		 */
		public final int binderIndex;

		/**
		 * @param item
		 * 		the model.
		 * @param itemPosition
		 * 		the position of the model in the list of models.
		 * @param binderList
		 * 		the list of binders associated with the item.
		 * @param binderIndex
		 * 		the index of the specific binder to use in the {@code binderList}.
		 */
		BinderResult(@Nullable final T item,
		             final int itemPosition,
		             @Nullable final List<Binder<? super T, ? extends VH>> binderList,
		             final int binderIndex) {
			this.item = item;
			this.itemPosition = itemPosition;
			this.binderList = binderList;
			this.binderIndex = binderIndex;
		}

		/**
		 * @return the binder to use.
		 */
		@Nullable
		public Binder<? super T, ? extends VH> getBinder() {
			return binderList != null && binderIndex >= 0 && binderIndex < binderList.size()
					? binderList.get(binderIndex) : null;
		}
	}
}
