package com.ang.acb.materialme;

import android.app.Application;

import timber.log.Timber;

public class XYZReaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Timber is a logger with a small, extensible API which provides utility on top of
        // Android's normal Log class. Behavior is added through Tree instances. You can
        // install an instance by calling Timber.plant(). Installation of Trees should be
        // done as early as possible. The onCreate() of your app is the most logical choice.
        // See: https://www.androidhive.info/2018/11/android-implementing-logging-using-timber
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }
}
