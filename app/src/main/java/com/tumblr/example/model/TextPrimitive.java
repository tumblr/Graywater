package com.tumblr.example.model;

/**
 * Created by ericleong on 3/13/16.
 */
public class TextPrimitive implements Primitive.Text {
	private final String string;

	public TextPrimitive(final String string) {
		this.string = string;
	}

	@Override
	public String getString() {
		return string;
	}
}
