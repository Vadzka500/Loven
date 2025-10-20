package com.sidspace.loven.authorization.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.model.UserManager
import com.sidspace.loven.authorization.data.repository.AuthorizationRepositoryImpl
import com.sidspace.loven.authorization.domain.repository.AuthorizationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideRepository(
        firebaseAuth: FirebaseAuth,
        userManager: UserManager,
        firestore: FirebaseFirestore
    ): AuthorizationRepository =
        AuthorizationRepositoryImpl(firebaseAuth, userManager, firestore)
}
