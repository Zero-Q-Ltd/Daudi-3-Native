package com.zeroq.daudi_3_native.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zeroq.daudi_3_native.di.qualifires.ViewModelKey
import com.zeroq.daudi_3_native.ui.login.LoginViewModel
import com.zeroq.daudi_3_native.ui.splash.SplashViewModel
import com.zeroq.daudi_3_native.viewmodel.DaudiViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(splashViewModel: SplashViewModel): ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: DaudiViewModelFactory): ViewModelProvider.Factory
}