package com.tumblr.example.binderlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.tumblr.example.binder.PaletteColorBinder;
import com.tumblr.example.binder.TextPrimitiveBinder;
import com.tumblr.example.model.Palette;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericleong on 3/28/16.
 */
public class PaletteItemBinder implements GraywaterAdapter.ItemBinder<Palette, PrimitiveViewHolder>,
		GraywaterAdapter.ActionListener<Palette, PrimitiveViewHolder> {
	private final TextPrimitiveBinder<Palette> mPaletteTextPrimitiveBinder;
	private final PaletteColorBinder mPaletteColorBinder;

	public PaletteItemBinder(final TextPrimitiveBinder<Palette> paletteTextPrimitiveBinder, final PaletteColorBinder paletteColorBinder) {
		mPaletteTextPrimitiveBinder = paletteTextPrimitiveBinder;
		mPaletteColorBinder = paletteColorBinder;
	}

	@NonNull
	@Override
	public List<GraywaterAdapter.Binder<? super Palette, ? extends PrimitiveViewHolder>> getBinderList(
			@NonNull final Palette model, final int position) {
		return new ArrayList<GraywaterAdapter.Binder<? super Palette, ? extends PrimitiveViewHolder>>() {{
			add(mPaletteTextPrimitiveBinder);

			for (int color : model.getColors()) {
				add(mPaletteColorBinder);
			}
		}};
	}

	@Override
	public void act(@NonNull final Palette model,
	                @NonNull final PrimitiveViewHolder holder,
	                @NonNull final View v,
	                @NonNull final List<GraywaterAdapter.Binder<? super Palette, ? extends PrimitiveViewHolder>> binders,
	                final int binderIndex,
	                @Nullable final Object obj) {

	}
}
