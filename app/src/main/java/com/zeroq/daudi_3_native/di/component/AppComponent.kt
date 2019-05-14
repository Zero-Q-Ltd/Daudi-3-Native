package com.zeroq.daudi_3_native.di.component

import com.zeroq.daudi_3_native.DaudiApplication
import com.zeroq.daudi_3_native.di.modules.ActivityBuilder
import com.zeroq.daudi_3_native.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuilder::class
    ]
)

interface AppComponent : AndroidInjector<DaudiApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: DaudiApplication): Builder

        fun build(): AppComponent
    }

    override fun inject(app: DaudiApplication)
}