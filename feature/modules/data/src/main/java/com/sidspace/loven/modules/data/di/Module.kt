package com.sidspace.loven.modules.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.loven.modules.data.repository.ModuleRepositoryImpl
import com.sidspace.loven.modules.domain.repository.ModuleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object Module {


    @Provides
    fun provideModuleRepository(firestore: FirebaseFirestore): ModuleRepository = ModuleRepositoryImpl(firestore)
}
