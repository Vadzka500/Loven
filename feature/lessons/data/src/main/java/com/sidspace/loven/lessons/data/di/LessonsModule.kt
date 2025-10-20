package com.sidspace.loven.lessons.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.model.UserManager
import com.sidspace.loven.lessons.data.repository.LessonsRepositoryImpl
import com.sidspace.loven.lessons.domain.repository.LessonsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class LessonsModule {

    @Provides
    fun provideRepository(firestore: FirebaseFirestore, userManager: UserManager): LessonsRepository =
        LessonsRepositoryImpl(firestore, userManager)
}
