package com.zeroq.daudi_3_native.di.fragment_modules

import com.zeroq.daudi_3_native.ui.loading.LoadingFragment
import com.zeroq.daudi_3_native.ui.processing.ProcessingFragment
import com.zeroq.daudi_3_native.ui.queued.QueuedFragment
import dagger.Module

@Suppress("unused")
@Module
abstract class FragmentMainModules {
    abstract fun contributeProcessingFragment(): ProcessingFragment
    abstract fun contributeLoadingFragment(): LoadingFragment
    abstract fun contributeQueuedFragment(): QueuedFragment
}