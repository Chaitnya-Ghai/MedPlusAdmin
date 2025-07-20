package com.example.medplusadmin.di

import com.example.medplusadmin.data.repository.CatalogRepositoryImpl
import com.example.medplusadmin.data.repository.ProfileRepositoryImpl
import com.example.medplusadmin.domain.repository.CatalogRepository
import com.example.medplusadmin.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(
        impl: CatalogRepositoryImpl
    ): CatalogRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

}