package com.tumblr.example.model;

import android.support.annotation.ColorRes;

import java.util.ArrayList;
import java.util.List;

/**
 * A palette has 3 colors.
 * <p/>
 * Created by ericleong on 3/13/16.
 */
public class Palette implements Primitive, Primitive.Text {

	private String name;

	private List<Integer> colors = new ArrayList<>();

	public Palette(String name, @ColorRes int... colors) {
		this.name = name;

		for (int color : colors) {
			this.colors.add(color);
		}
	}

	@Override
	public String getString() {
		return name;
	}

	public List<Integer> getColors() {
		return colors;
	}
}
