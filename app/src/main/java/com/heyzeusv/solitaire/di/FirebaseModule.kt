package com.heyzeusv.solitaire.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     *  Provides [FirebaseAuth] which manages users.
     */
    @Provides
    fun provideAuth(): FirebaseAuth = Firebase.auth

    /**
     *  Provides [FirebaseFirestore] which is used to store users, their stats, and unique usernames.
     */
    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
}