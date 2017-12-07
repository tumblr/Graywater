package com.tumblr.example;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tumblr.example.dagger.PerActivity;
import com.tumblr.example.model.Primitive;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

/**
 * Example adapter.
 * <p>
 * Created by ericleong on 3/13/16.
 */
@PerActivity
public class PrimitiveAdapter extends GraywaterAdapter<
		Primitive,
		PrimitiveViewHolder,
		GraywaterAdapter.Binder<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>,
		Class<? extends Primitive>> {

	@NonNull
	private final Map<Class<? extends Primitive>,
			Provider<ItemBinder<
					? extends Primitive,
					? extends PrimitiveViewHolder,
					? extends Binder<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>>> mItemBinderMap;

	@NonNull
	private final Map<Class<? extends Primitive>,
			Provider<GraywaterAdapter.ActionListener<? extends Primitive,
					PrimitiveViewHolder,
					? extends PrimitiveViewHolder>>> mActionListenerMap;

	@Inject
	public PrimitiveAdapter(final Map<Class<? extends PrimitiveViewHolder>, ViewHolderCreator> viewHolderCreatorMapClass,
	                        @NonNull final Map<Class<? extends Primitive>,
			                        Provider<ItemBinder<
					                        ? extends Primitive,
					                        ? extends PrimitiveViewHolder,
					                        ? extends Binder<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>>>
			                        itemBinderMap,
	                        @NonNull final Map<Class<? extends Primitive>,
			                        Provider<GraywaterAdapter.ActionListener<
					                        ? extends Primitive,
					                        PrimitiveViewHolder,
					                        ? extends PrimitiveViewHolder>>> actionListenerMap) {

		for (Map.Entry<Class<? extends PrimitiveViewHolder>, ViewHolderCreator> entry : viewHolderCreatorMapClass.entrySet()) {
			register(entry.getValue(), entry.getKey());
		}

		mItemBinderMap = itemBinderMap;
		mActionListenerMap = actionListenerMap;
	}

	@Nullable
	@Override
	protected ItemBinder<? extends Primitive,
			? extends PrimitiveViewHolder,
			? extends Binder<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>
	getItemBinder(final Primitive model) {
		final Class<? extends Primitive> modelType = getModelType(model);

		return mItemBinderMap.get(modelType).get();
	}

	@Nullable
	@Override
	protected ActionListener<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>
	getActionListener(final Primitive model) {
		final Class<? extends Primitive> modelType = getModelType(model);
		final Provider<ActionListener<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>
				provider = mActionListenerMap.get(modelType);

		return provider != null ? provider.get() : null;
	}

	@NonNull
	@Override
	protected Class<? extends Primitive> getModelType(final Primitive model) {
		return model.getClass();
	}
}
