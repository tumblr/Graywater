package com.tumblr.example.binder;

import android.support.annotation.NonNull;
import com.tumblr.example.R;
import com.tumblr.example.model.Primitive;
import com.tumblr.example.viewholder.HeaderViewHolder;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import javax.inject.Provider;
import java.util.List;

/**
 * Created by ericleong on 3/13/16.
 */
public class HeaderBinder implements GraywaterAdapter.Binder<Primitive.Header,PrimitiveViewHolder,HeaderViewHolder> {

	@Override
	public int getViewType(final Primitive.Header model) {
		return R.layout.item_header;
	}

	@Override
	public void prepare(@NonNull final Primitive.Header model,
	                    final List<Provider<GraywaterAdapter.Binder<
			                    ? super Primitive.Header, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                    final int binderIndex) {

	}

	@Override
	public void bind(@NonNull final Primitive.Header model,
	                 @NonNull final HeaderViewHolder holder,
	                 @NonNull final List<Provider<GraywaterAdapter.Binder<
			                 ? super Primitive.Header, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                 final int binderIndex,
	                 @NonNull final GraywaterAdapter.ActionListener<
			                 Primitive.Header, PrimitiveViewHolder, HeaderViewHolder> actionListener) {

	}

	@Override
	public void unbind(@NonNull final HeaderViewHolder holder) {

	}
}
