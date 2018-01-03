package com.tumblr.example.dagger;

import com.tumblr.example.App;
import com.tumblr.example.dagger.module.ActivityBindingModule;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

import javax.inject.Singleton;

/**
 * Created by ericleong on 12/6/17.
 */
@Singleton
@Component(modules = {
		AndroidSupportInjectionModule.class,
		ActivityBindingModule.class
})
public interface AppComponent extends AndroidInjector<App> {
	@Component.Builder
	abstract class Builder extends AndroidInjector.Builder<App> {}
}
