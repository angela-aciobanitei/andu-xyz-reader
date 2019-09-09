package com.ang.acb.materialme;

import android.app.Activity;
import android.app.Application;

import com.ang.acb.materialme.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

/**
 * When using Dagger for injecting Activity objects, you need to make your Application
 * implement HasAndroidInjector and @Inject a DispatchingAndroidInjector<Object> to
 * return from the androidInjector() method. See: https://dagger.dev/android.html.
 */
public class XYZReaderApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerAppComponent.builder().application(this).build().inject(this);

        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }


}
