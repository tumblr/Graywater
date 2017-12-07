package com.tumblr.example.dagger.module;

import com.tumblr.example.binderlist.ColorNamePrimitiveItemBinder;
import com.tumblr.example.binderlist.HeaderPrimitiveItemBinder;
import com.tumblr.example.binderlist.PaletteItemBinder;
import com.tumblr.example.dagger.PerActivity;
import com.tumblr.example.dagger.key.PrimitiveItemBinderKey;
import com.tumblr.example.model.ColorNamePrimitive;
import com.tumblr.example.model.Palette;
import com.tumblr.example.model.Primitive;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by ericleong on 12/6/17.
 */
@Module
public abstract class ItemBinderModule {
	@PerActivity
	@Binds
	@IntoMap
	@PrimitiveItemBinderKey(ColorNamePrimitive.class)
	abstract GraywaterAdapter.ItemBinder<
			? extends Primitive,
			? extends PrimitiveViewHolder,
			? extends GraywaterAdapter.Binder<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>
	bindsColorNamePrimitiveItemBinder(ColorNamePrimitiveItemBinder colorNamePrimitiveItemBinder);

	@PerActivity
	@Binds
	@IntoMap
	@PrimitiveItemBinderKey(Primitive.Header.class)
	abstract GraywaterAdapter.ItemBinder<
			? extends Primitive,
			? extends PrimitiveViewHolder,
			? extends GraywaterAdapter.Binder<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>
	bindsHeaderPrimitiveItemBinder(HeaderPrimitiveItemBinder headerPrimitiveItemBinder);

	@PerActivity
	@Binds
	@IntoMap
	@PrimitiveItemBinderKey(Palette.class)
	abstract GraywaterAdapter.ItemBinder<
			? extends Primitive,
			? extends PrimitiveViewHolder,
			? extends GraywaterAdapter.Binder<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>>
	bindsPaletteItemBinder(PaletteItemBinder paletteItemBinder);
}
