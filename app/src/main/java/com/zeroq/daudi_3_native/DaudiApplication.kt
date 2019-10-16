package com.zeroq.daudi_3_native


import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.data.repository.AdminRepository
import com.zeroq.daudi_3_native.di.DaggerAppComponent
import com.zeroq.daudi_3_native.ui.login.LoginActivity
import com.zeroq.daudi_3_native.utils.ReleaseTree
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class DaudiApplication : DaggerApplication() {

    private lateinit var fireAuthListener: FirebaseAuth.AuthStateListener
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var adminRepository: AdminRepository

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()

        appComponent.inject(this)
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        authChange()
    }

    fun authChange() {
        fireAuthListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null) {
                Timber.d("login occured")
            }

            if (it.currentUser == null) {
                LoginActivity.startActivity(this)
            }

        }
        firebaseAuth.addAuthStateListener(fireAuthListener)
    }
}