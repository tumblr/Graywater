package com.tumblr.example.viewholder;

import android.view.View;
import com.tumblr.example.R;
import com.tumblr.example.model.ColorNamePrimitive;
import com.tumblr.graywater.GraywaterAdapter;

/**
 * Created by ericleong on 3/13/16.
 */
public class ColorPrimitiveViewHolder extends PrimitiveViewHolder {

	private GraywaterAdapter.ActionListenerDelegate<ColorNamePrimitive, PrimitiveViewHolder, ColorPrimitiveViewHolder>
			mActionListenerDelegate = new GraywaterAdapter.ActionListenerDelegate<>();

	private final View view;

	public ColorPrimitiveViewHolder(final View itemView) {
		super(itemView);
		view = itemView.findViewById(R.id.color);
	}

	public View getView() {
		return view;
	}

	public GraywaterAdapter.ActionListenerDelegate<ColorNamePrimitive, PrimitiveViewHolder, ColorPrimitiveViewHolder>
	getActionListenerDelegate() {
		return mActionListenerDelegate;
	}
}
