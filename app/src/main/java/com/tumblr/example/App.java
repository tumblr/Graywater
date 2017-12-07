package com.tumblr.example;

import com.tumblr.example.dagger.DaggerAppComponent;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Created by ericleong on 12/6/17.
 */
public class App extends DaggerApplication {
	@Override
	protected AndroidInjector<App> applicationInjector() {
		return DaggerAppComponent.builder().create(this);
	}
}
