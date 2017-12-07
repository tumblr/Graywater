package com.tumblr.example.viewholdercreator;

import android.view.ViewGroup;
import com.tumblr.example.R;
import com.tumblr.example.viewholder.TextPrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

import javax.inject.Inject;

/**
 * Created by ericleong on 3/15/16.
 */
public class TextPrimitiveViewHolderCreator implements GraywaterAdapter.ViewHolderCreator {

	@Inject
	public TextPrimitiveViewHolderCreator() {

	}

	@Override
	public TextPrimitiveViewHolder create(final ViewGroup parent) {
		return new TextPrimitiveViewHolder(GraywaterAdapter.inflate(parent, R.layout.item_text));
	}

	@Override
	public int getViewType() {
		return R.layout.item_text;
	}
}
