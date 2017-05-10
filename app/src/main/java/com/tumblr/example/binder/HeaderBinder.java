package com.tumblr.example.binder;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import com.tumblr.graywater.GraywaterAdapter;
import com.tumblr.example.R;
import com.tumblr.example.model.Primitive;
import com.tumblr.example.viewholder.HeaderViewHolder;

import java.util.List;

/**
 * Created by ericleong on 3/13/16.
 */
public class HeaderBinder implements GraywaterAdapter.Binder<Primitive.Header, HeaderViewHolder> {

	@NonNull
	@Override
	public Class<HeaderViewHolder> getViewHolderType() {
		return HeaderViewHolder.class;
	}

	@Override
	public void prepare(@NonNull final Primitive.Header model,
	                    final List<GraywaterAdapter.Binder<? super Primitive.Header, ? extends HeaderViewHolder>> binders,
	                    final int binderIndex) {

	}

	@Override
	public void bind(@NonNull final Primitive.Header model,
	                 @NonNull final HeaderViewHolder holder,
	                 @NonNull final List<GraywaterAdapter.Binder<? super Primitive.Header, ? extends HeaderViewHolder>> binders,
	                 final int binderIndex,
	                 @NonNull final GraywaterAdapter.ActionListener<Primitive.Header, HeaderViewHolder> actionListener) {

	}

	@Override
	public void unbind(@NonNull final HeaderViewHolder holder) {

	}
}
