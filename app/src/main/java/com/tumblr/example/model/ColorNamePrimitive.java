package com.tumblr.example.model;

import android.support.annotation.ColorRes;

/**
 * Created by ericleong on 3/13/16.
 */
public class ColorNamePrimitive implements Primitive.Color, Primitive.Text {
	@ColorRes
	private int color;

	private final String string;

	public ColorNamePrimitive(final int color, final String string) {
		this.color = color;
		this.string = string;
	}

	public void setColor(@ColorRes final int color) {
		this.color = color;
	}

	@ColorRes
	public int getColor() {
		return color;
	}

	public String getString() {
		return string;
	}
}
