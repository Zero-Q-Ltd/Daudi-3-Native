package com.zeroq.daudi_3_native

import com.zeroq.daudi_3_native.di.component.DaggerAppComponent
import com.zeroq.daudi_3_native.utils.ReleaseTree
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class DaudiApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()

        appComponent.inject(this)
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}