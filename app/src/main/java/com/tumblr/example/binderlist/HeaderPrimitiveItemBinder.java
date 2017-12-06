package com.tumblr.example.binderlist;

import android.support.annotation.NonNull;
import com.tumblr.example.binder.HeaderBinder;
import com.tumblr.example.model.Primitive;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericleong on 3/28/16.
 */
public class HeaderPrimitiveItemBinder implements
		GraywaterAdapter.ItemBinder<Primitive.Header, PrimitiveViewHolder,
				GraywaterAdapter.Binder<Primitive.Header, PrimitiveViewHolder, ? extends PrimitiveViewHolder>> {
	private final Provider<HeaderBinder> mHeaderBinder;

	public HeaderPrimitiveItemBinder(final Provider<HeaderBinder> headerBinder) {
		mHeaderBinder = headerBinder;
	}

	@NonNull
	@Override
	public List<Provider<
			? extends GraywaterAdapter.Binder<Primitive.Header, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>>
	getBinderList(@NonNull final Primitive.Header model, final int position) {
		return new ArrayList<Provider<
				? extends GraywaterAdapter.Binder<Primitive.Header, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>>() {{
			add(mHeaderBinder);
		}};
	}
}
