package com.zeroq.daudi_3_native.di.modules

import com.google.firebase.firestore.CollectionReference
import dagger.Module
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton


@Module
class FirestoreModule {


    @Singleton
    @Provides
    fun providesFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    @Named("admins")
    fun providesAdmins(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("admins")
    }
}