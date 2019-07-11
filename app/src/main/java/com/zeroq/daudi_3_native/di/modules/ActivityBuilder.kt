package com.zeroq.daudi_3_native.di.modules

import com.zeroq.daudi_3_native.di.fragment_modules.FragmentMainModules
import com.zeroq.daudi_3_native.ui.MainActivity
import com.zeroq.daudi_3_native.ui.activate.ActivateActivity
import com.zeroq.daudi_3_native.ui.login.LoginActivity
import com.zeroq.daudi_3_native.ui.splash.SplashActivity
import com.zeroq.daudi_3_native.ui.truck_detail.TruckDetailActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [FragmentMainModules::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun bindActivateActivity(): ActivateActivity

    @ContributesAndroidInjector
    internal abstract fun bindTruckDetailActivity(): TruckDetailActivity
}