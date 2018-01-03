package com.tumblr.example.binder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tumblr.example.R;
import com.tumblr.example.model.ColorNamePrimitive;
import com.tumblr.example.viewholder.ColorPrimitiveViewHolder;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

/**
 * Created by ericleong on 3/24/16.
 */
public class ColorNameToastBinder implements GraywaterAdapter.Binder<ColorNamePrimitive, PrimitiveViewHolder, ColorPrimitiveViewHolder> {

	@Inject
	public ColorNameToastBinder() {

	}

	@Override
	public int getViewType(final ColorNamePrimitive model) {
		return R.layout.item_color;
	}

	@Override
	public void prepare(@NonNull final ColorNamePrimitive model,
	                    final List<Provider<GraywaterAdapter.Binder<
			                    ? super ColorNamePrimitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                    final int binderIndex) {

	}

	@Override
	public void bind(@NonNull final ColorNamePrimitive model,
	                 @NonNull final ColorPrimitiveViewHolder holder,
	                 @NonNull final List<Provider<GraywaterAdapter.Binder<
			                 ? super ColorNamePrimitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                 final int binderIndex,
	                 @Nullable final GraywaterAdapter.ActionListener<
			                 ColorNamePrimitive, PrimitiveViewHolder, ColorPrimitiveViewHolder> actionListener) {
		holder.getView().setBackgroundColor(holder.getView().getResources().getColor(model.getColor()));
		holder.getActionListenerDelegate().update(actionListener, model, holder, binderList, binderIndex, null);
		holder.getView().setOnClickListener(holder.getActionListenerDelegate());
	}

	@Override
	public void unbind(@NonNull final ColorPrimitiveViewHolder holder) {
		holder.getView().setOnClickListener(null);
	}
}
