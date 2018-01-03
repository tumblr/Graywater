package com.tumblr.example.binderlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.tumblr.example.binder.PaletteColorBinder;
import com.tumblr.example.binder.TextPrimitiveBinder;
import com.tumblr.example.dagger.PerActivity;
import com.tumblr.example.model.Palette;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericleong on 3/28/16.
 */
@PerActivity
public class PaletteItemBinder implements GraywaterAdapter.ItemBinder<Palette, PrimitiveViewHolder,
		GraywaterAdapter.Binder<Palette, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>,
		GraywaterAdapter.ActionListener<Palette, PrimitiveViewHolder, PrimitiveViewHolder> {
	private final Provider<TextPrimitiveBinder<Palette>> mPaletteTextPrimitiveBinder;
	private final Provider<PaletteColorBinder> mPaletteColorBinder;

	@Inject
	public PaletteItemBinder(final Provider<TextPrimitiveBinder<Palette>> paletteTextPrimitiveBinder,
	                         final Provider<PaletteColorBinder> paletteColorBinder) {
		mPaletteTextPrimitiveBinder = paletteTextPrimitiveBinder;
		mPaletteColorBinder = paletteColorBinder;
	}

	@NonNull
	@Override
	public List<Provider<? extends GraywaterAdapter.Binder<Palette, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>>
	getBinderList(@NonNull final Palette model, final int position) {
		return new ArrayList<Provider<? extends GraywaterAdapter.Binder<Palette, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>>() {{
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
	                @NonNull final List<Provider<GraywaterAdapter.Binder<
			                ? super Palette, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                final int binderIndex,
	                @Nullable final Object obj) {

	}
}
