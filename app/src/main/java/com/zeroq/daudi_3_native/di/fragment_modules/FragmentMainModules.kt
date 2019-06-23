package com.zeroq.daudi_3_native.di.fragment_modules

import com.zeroq.daudi_3_native.ui.loading.LoadingFragment
import com.zeroq.daudi_3_native.ui.processing.ProcessingFragment
import com.zeroq.daudi_3_native.ui.queued.QueuedFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentMainModules {
    @ContributesAndroidInjector
    abstract fun contributeProcessingFragment(): ProcessingFragment

    @ContributesAndroidInjector
    abstract fun contributeLoadingFragment(): LoadingFragment

    @ContributesAndroidInjector
    abstract fun contributeQueuedFragment(): QueuedFragment
}