package com.zeroq.daudi_3_native.di.modules

import com.zeroq.daudi_3_native.ui.MainActivity
import com.zeroq.daudi_3_native.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindSplashActivity(): SplashActivity
}