package com.sidspace.loven.game.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.model.UserManager
import com.sidspace.game.domain.repository.GameRepository
import com.sidspace.loven.game.data.repository.GameRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GameModule {

    @Provides
    fun provideRepository(firestore: FirebaseFirestore, userManager: UserManager): GameRepository =
        GameRepositoryImpl(firestore, userManager)
}
