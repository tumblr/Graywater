package com.tumblr.example.model;

import android.support.annotation.ColorRes;

/**
 * Created by ericleong on 3/13/16.
 */
public interface Primitive {

	interface Text extends Primitive {
		String getString();
	}

	interface Color extends Primitive {
		@ColorRes
		int getColor();
	}

	class Header implements Primitive {
		// Dummy marker class
	}
}
