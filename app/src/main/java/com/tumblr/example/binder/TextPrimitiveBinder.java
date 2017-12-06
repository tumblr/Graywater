package com.tumblr.example.binder;

import android.support.annotation.NonNull;
import com.tumblr.example.R;
import com.tumblr.example.model.Primitive;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.example.viewholder.TextPrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import javax.inject.Provider;
import java.util.List;

/**
 * Created by ericleong on 3/13/16.
 */
public class TextPrimitiveBinder<T extends Primitive.Text>
		implements GraywaterAdapter.Binder<T,PrimitiveViewHolder,TextPrimitiveViewHolder> {
	@Override
	public int getViewType(final T model) {
		return R.layout.item_text;
	}

	@Override
	public void prepare(@NonNull final T model,
	                    final List<Provider<GraywaterAdapter.Binder<
			                    ? super T, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                    final int binderIndex) {

	}

	@Override
	public void bind(@NonNull final T model,
	                 @NonNull final TextPrimitiveViewHolder holder,
	                 @NonNull final List<Provider<GraywaterAdapter.Binder<
			                 ? super T, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                 final int binderIndex,
	                 @NonNull final GraywaterAdapter.ActionListener<T, PrimitiveViewHolder, TextPrimitiveViewHolder> actionListener) {
		holder.getTextView().setText(model.getString());
	}

	@Override
	public void unbind(@NonNull final TextPrimitiveViewHolder holder) {

	}
}
