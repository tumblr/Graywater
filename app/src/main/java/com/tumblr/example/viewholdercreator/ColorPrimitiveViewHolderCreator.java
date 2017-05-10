package com.tumblr.example.viewholdercreator;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import com.tumblr.example.R;
import com.tumblr.example.viewholder.ColorPrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

/**
 * Created by ericleong on 3/15/16.
 */
public class ColorPrimitiveViewHolderCreator implements GraywaterAdapter.ViewHolderCreator {
	@Override
	public ColorPrimitiveViewHolder create(final ViewGroup parent) {
		return new ColorPrimitiveViewHolder(GraywaterAdapter.inflate(parent, R.layout.item_color));
	}

	@Override
	public int getViewType() {
		return R.layout.item_color;
	}
}
