package com.zeroq.daudi_3_native.di.modules

import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

/**
 * This will expose class modules
 * eg, network, storageModule, etc
 * **/
@Module(
    includes = [
        ViewModelModule::class,
        AuthModule::class,
        FirestoreModule::class
    ]
)
class AppModule {

    @Provides
    @Singleton
    fun providesEventBus(): EventBus {
        return EventBus.getDefault()
    }
}