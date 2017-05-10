package com.tumblr.example.viewholder;

import android.view.View;
import android.widget.TextView;
import com.tumblr.example.R;

/**
 * Created by ericleong on 3/13/16.
 */
public class TextPrimitiveViewHolder extends PrimitiveViewHolder {
	private final TextView textView;

	public TextPrimitiveViewHolder(final View itemView) {
		super(itemView);
		textView = (TextView) itemView.findViewById(R.id.text);
	}

	public TextView getTextView() {
		return textView;
	}
}
