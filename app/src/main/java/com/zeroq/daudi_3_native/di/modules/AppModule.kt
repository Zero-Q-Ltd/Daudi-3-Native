package com.zeroq.daudi_3_native.di.modules

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.core.app.AlarmManagerCompat
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
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

    @Provides
    @Singleton
    fun providesAlarmManager(app: Application): AlarmManager {
        return app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
}