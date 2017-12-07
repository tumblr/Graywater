package com.tumblr.example.dagger.module;

import com.tumblr.example.binderlist.ColorNamePrimitiveItemBinder;
import com.tumblr.example.dagger.PerActivity;
import com.tumblr.example.dagger.key.PrimitiveItemBinderKey;
import com.tumblr.example.model.ColorNamePrimitive;
import com.tumblr.example.model.Primitive;
import com.tumblr.example.viewholder.PrimitiveViewHolder;
import com.tumblr.graywater.GraywaterAdapter;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by ericleong on 12/7/17.
 */
@Module
public abstract class ActionListenerModule {
	@PerActivity
	@Binds
	@IntoMap
	@PrimitiveItemBinderKey(ColorNamePrimitive.class)
	abstract GraywaterAdapter.ActionListener<? extends Primitive, PrimitiveViewHolder, ? extends PrimitiveViewHolder>
	bindsColorNamePrimitiveActionListener(ColorNamePrimitiveItemBinder colorNamePrimitiveItemBinder);
}
