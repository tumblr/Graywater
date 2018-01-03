package com.tumblr.example.dagger.module;

import com.tumblr.example.MainActivity;
import com.tumblr.example.dagger.PerActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ericleong on 12/6/17.
 */
@Module
public abstract class ActivityBindingModule {
	@PerActivity
	@ContributesAndroidInjector(
			modules = {
					ItemBinderModule.class,
					ViewHolderCreatorModule.class,
					ActionListenerModule.class
			}
	)
	abstract MainActivity contributeMainActivityInjector();
}