package com.zeroq.daudi_3_native.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zeroq.daudi_3_native.di.qualifires.ViewModelKey
import com.zeroq.daudi_3_native.viewmodel.AuthenticationViewModel
import com.zeroq.daudi_3_native.viewmodel.DaudiViewModelFactory
import com.zeroq.daudi_3_native.viewmodel.UserViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(AuthenticationViewModel::class)
    abstract fun bindAuthenticationViewModel(authenticationViewModel: AuthenticationViewModel): ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: DaudiViewModelFactory): ViewModelProvider.Factory
}