package com.tumblr.example.viewholdercreator;

import android.view.ViewGroup;
import com.tumblr.example.R;
import com.tumblr.example.viewholder.HeaderViewHolder;
import com.tumblr.graywater.GraywaterAdapter;

/**
 * Created by ericleong on 3/15/16.
 */
public class HeaderViewHolderCreator implements GraywaterAdapter.ViewHolderCreator {
	@Override
	public HeaderViewHolder create(final ViewGroup parent) {
		return new HeaderViewHolder(GraywaterAdapter.inflate(parent, R.layout.item_header));
	}

	@Override
	public int getViewType() {
		return R.layout.item_header;
	}
}
