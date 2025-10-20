package com.sidspace.loven.home.di

import com.sidspace.loven.home.data.repository.HomeRepositoryImpl
import com.sidspace.loven.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    abstract fun provideRepository(impl: HomeRepositoryImpl): HomeRepository
}
