package com.zeroq.daudi_3_native.di.modules

import dagger.Module

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
}