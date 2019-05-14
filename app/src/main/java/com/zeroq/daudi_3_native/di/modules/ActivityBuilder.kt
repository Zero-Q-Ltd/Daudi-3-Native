package com.zeroq.daudi_3_native.di.modules

import com.zeroq.daudi_3_native.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    internal abstract fun bindMainActivity(): MainActivity
}