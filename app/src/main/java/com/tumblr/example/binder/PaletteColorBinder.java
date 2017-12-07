package com.tumblr.example.binder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;
import com.tumblr.example.R;
import com.tumblr.example.model.Palette;
import com.tumblr.example.viewholder.ColorPrimitiveViewHolder;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

/**
 * Created by ericleong on 3/13/16.
 */
public class PaletteColorBinder implements GraywaterAdapter.Binder<Palette, PrimitiveViewHolder, ColorPrimitiveViewHolder> {

	@Inject
	public PaletteColorBinder() {

	}

	@Override
	public int getViewType(final Palette model) {
		return R.layout.item_color;
	}

	@Override
	public void prepare(@NonNull final Palette model,
	                    final List<Provider<GraywaterAdapter.Binder<
			                    ? super Palette, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                    final int binderIndex) {

	}

	@Override
	public void bind(@NonNull final Palette model,
	                 @NonNull final ColorPrimitiveViewHolder holder,
	                 @NonNull final List<Provider<GraywaterAdapter.Binder<
			                 ? super Palette, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>> binderList,
	                 final int binderIndex,
	                 @Nullable final GraywaterAdapter.ActionListener<
			                 Palette, PrimitiveViewHolder, ColorPrimitiveViewHolder> actionListener) {
		holder.getView().setBackgroundColor(holder.getView().getResources().getColor(model.getColors().get
				(binderIndex - 1)));

		holder.getView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				PaletteColorBinder.this.onClick(v, model, holder);
			}
		});
	}

	@Override
	public void unbind(@NonNull final ColorPrimitiveViewHolder holder) {
		holder.getView().setOnClickListener(null);
	}

	public void onClick(final View v, final Palette model, final ColorPrimitiveViewHolder holder) {
		Toast.makeText(v.getContext(), model.getString(), Toast.LENGTH_SHORT).show();
	}
}
