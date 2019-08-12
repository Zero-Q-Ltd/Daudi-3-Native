package com.zeroq.daudi_3_native.di.modules

import com.zeroq.daudi_3_native.broadcasts.TruckExpireBroadCast
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class BroadCastModule {

    @ContributesAndroidInjector
    abstract fun contributeTruckExpireBroadCast(): TruckExpireBroadCast
}