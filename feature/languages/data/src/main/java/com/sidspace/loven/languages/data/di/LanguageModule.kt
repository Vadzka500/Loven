package com.sidspace.loven.languages.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.loven.languages.data.repository.LanguageRepositoryImpl
import com.sidspace.loven.languages.domain.repository.LanguageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LanguageModule {

    @Provides
    fun provideRepository(firestore: FirebaseFirestore): LanguageRepository = LanguageRepositoryImpl(firestore)

}
