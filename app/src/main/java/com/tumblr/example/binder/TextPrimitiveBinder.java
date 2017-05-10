package com.tumblr.example.binder;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import com.tumblr.graywater.GraywaterAdapter;
import com.tumblr.example.R;
import com.tumblr.example.model.Primitive;
import com.tumblr.example.viewholder.TextPrimitiveViewHolder;

import java.util.List;

/**
 * Created by ericleong on 3/13/16.
 */
public class TextPrimitiveBinder<U extends Primitive.Text> implements GraywaterAdapter.Binder<U, TextPrimitiveViewHolder> {

	@NonNull
	@Override
	public Class<TextPrimitiveViewHolder> getViewHolderType() {
		return TextPrimitiveViewHolder.class;
	}

	@Override
	public void prepare(@NonNull final U model,
	                    final List<GraywaterAdapter.Binder<? super U, ? extends TextPrimitiveViewHolder>> binders,
	                    final int binderIndex) {

	}

	@Override
	public void bind(@NonNull final U model,
	                 @NonNull final TextPrimitiveViewHolder holder,
	                 @NonNull final List<GraywaterAdapter.Binder<? super U, ? extends TextPrimitiveViewHolder>> binders,
	                 final int binderIndex,
	                 @NonNull final GraywaterAdapter.ActionListener<U, TextPrimitiveViewHolder> actionListener) {
		holder.getTextView().setText(model.getString());
	}

	@Override
	public void unbind(@NonNull final TextPrimitiveViewHolder holder) {

	}
}
