package com.tumblr.example;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.tumblr.example.model.ColorNamePrimitive;
import com.tumblr.example.model.Palette;
import com.tumblr.example.model.Primitive;
import dagger.android.support.DaggerAppCompatActivity;

import javax.inject.Inject;

public class MainActivity extends DaggerAppCompatActivity {

	@Inject
	PrimitiveAdapter mPrimitiveAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

		if (recyclerView != null) {
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.setItemAnimator(new DefaultItemAnimator());

			// A header has nothing special
			mPrimitiveAdapter.add(new Primitive.Header());

			// Reds
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.red_base_variant_0, "dark red"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.red_base_variant_1, "red"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.red_base_variant_2, "bright red"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.red_base_variant_3, "shy red"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.red_base_variant_4, "embarrassed red"));
			mPrimitiveAdapter.add(new Palette("Red Palette", R.color.red_base_variant_0, R.color.red_base_variant_2, R.color.red_base_variant_4));

			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.yellow_base_variant_0, "dark yellow"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.yellow_base_variant_1, "yellow"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.yellow_base_variant_2, "bright yellow"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.yellow_base_variant_3, "shy yellow"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.yellow_base_variant_4, "embarrassed yellow"));
			mPrimitiveAdapter.add(new Palette("Yellow Palette",
					R.color.yellow_base_variant_0, R.color.yellow_base_variant_2, R.color.yellow_base_variant_4));

			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.green_base_variant_0, "dark green"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.green_base_variant_1, "green"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.green_base_variant_2, "bright green"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.green_base_variant_3, "shy green"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.green_base_variant_4, "embarrassed green"));
			mPrimitiveAdapter.add(new Palette("Green Palette",
							R.color.green_base_variant_0, R.color.green_base_variant_2, R.color.green_base_variant_4));

			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.blue_base_variant_0, "dark blue"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.blue_base_variant_1, "blue"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.blue_base_variant_2, "bright blue"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.blue_base_variant_3, "shy blue"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.blue_base_variant_4, "embarrassed blue"));
			mPrimitiveAdapter.add(new Palette("Blue Palette",
							R.color.blue_base_variant_0, R.color.blue_base_variant_2, R.color.blue_base_variant_4));

			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.purple_base_variant_0, "dark purple"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.purple_base_variant_1, "purple"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.purple_base_variant_2, "bright purple"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.purple_base_variant_3, "shy purple"));
			mPrimitiveAdapter.add(new ColorNamePrimitive(R.color.purple_base_variant_4, "embarrassed purple"));
			mPrimitiveAdapter.add(new Palette("Purple Palette",
							R.color.purple_base_variant_0, R.color.purple_base_variant_2, R.color.purple_base_variant_4));

			mPrimitiveAdapter.add(new Palette("Rainbow",
							R.color.red_base_variant_0, R.color.yellow_base_variant_0, R.color.green_base_variant_0,
							R.color.blue_base_variant_0, R.color.purple_base_variant_0));

			mPrimitiveAdapter.add(new Palette("Strange Rainbow",
					R.color.red_base_variant_0, R.color.yellow_base_variant_1, R.color.green_base_variant_2,
					R.color.blue_base_variant_3, R.color.purple_base_variant_4));

			recyclerView.setAdapter(mPrimitiveAdapter);
		}
	}
}
