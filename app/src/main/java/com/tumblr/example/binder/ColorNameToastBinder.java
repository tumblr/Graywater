package com.tumblr.example.binder;

import android.support.annotation.NonNull;
import com.tumblr.example.model.ColorNamePrimitive;
import com.tumblr.example.viewholder.ColorPrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import java.util.List;

/**
 * Created by ericleong on 3/24/16.
 */
public class ColorNameToastBinder implements GraywaterAdapter.Binder<ColorNamePrimitive, ColorPrimitiveViewHolder> {

	@NonNull
	@Override
	public Class<ColorPrimitiveViewHolder> getViewHolderType() {
		return ColorPrimitiveViewHolder.class;
	}

	@Override
	public void prepare(@NonNull final ColorNamePrimitive model,
	                    final List<GraywaterAdapter.Binder<? super ColorNamePrimitive, ? extends ColorPrimitiveViewHolder>> binders,
	                    final int binderIndex) {

	}

	@Override
	public void bind(@NonNull final ColorNamePrimitive model,
	                 @NonNull final ColorPrimitiveViewHolder holder,
	                 @NonNull final List<GraywaterAdapter.Binder<? super ColorNamePrimitive, ? extends ColorPrimitiveViewHolder>> binders,
	                 final int binderIndex,
	                 @NonNull final GraywaterAdapter.ActionListener<ColorNamePrimitive, ColorPrimitiveViewHolder> actionListener) {
		holder.getView().setBackgroundColor(holder.getView().getResources().getColor(model.getColor()));
		holder.getActionListenerDelegate().update(actionListener, model, holder, binders, binderIndex, null);
		holder.getView().setOnClickListener(holder.getActionListenerDelegate());
	}

	@Override
	public void unbind(@NonNull final ColorPrimitiveViewHolder holder) {
		holder.getView().setOnClickListener(null);
	}
}
