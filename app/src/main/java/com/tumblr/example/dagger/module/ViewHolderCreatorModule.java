package com.tumblr.example.dagger.module;

import com.tumblr.example.dagger.key.PrimitiveCreatorKey;
import com.tumblr.example.viewholder.ColorPrimitiveViewHolder;
import com.tumblr.example.viewholder.HeaderViewHolder;
import com.tumblr.example.viewholder.TextPrimitiveViewHolder;
import com.tumblr.example.viewholdercreator.ColorPrimitiveViewHolderCreator;
import com.tumblr.example.viewholdercreator.HeaderViewHolderCreator;
import com.tumblr.example.viewholdercreator.TextPrimitiveViewHolderCreator;
import com.tumblr.graywater.GraywaterAdapter;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by ericleong on 12/6/17.
 */
@Module
public abstract class ViewHolderCreatorModule {
	@Binds
	@IntoMap
	@PrimitiveCreatorKey(TextPrimitiveViewHolder.class)
	abstract GraywaterAdapter.ViewHolderCreator bindsTextPrimitiveViewHolderCreator(
			TextPrimitiveViewHolderCreator textPrimitiveViewHolderCreator);

	@Binds
	@IntoMap
	@PrimitiveCreatorKey(HeaderViewHolder.class)
	abstract GraywaterAdapter.ViewHolderCreator bindsHeaderViewHolderCreator(HeaderViewHolderCreator headerViewHolderCreator);

	@Binds
	@IntoMap
	@PrimitiveCreatorKey(ColorPrimitiveViewHolder.class)
	abstract GraywaterAdapter.ViewHolderCreator bindsColorPrimitiveViewHolderCreator(
			ColorPrimitiveViewHolderCreator colorPrimitiveViewHolderCreator);
}
