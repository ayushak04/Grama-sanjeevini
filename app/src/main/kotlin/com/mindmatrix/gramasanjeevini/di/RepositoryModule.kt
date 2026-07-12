package com.mindmatrix.gramasanjeevini.di

import com.mindmatrix.gramasanjeevini.auth.data.AuthRepository
import com.mindmatrix.gramasanjeevini.auth.data.FirebaseAuthRepository
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
    abstract fun bindAuthRepository(repository: FirebaseAuthRepository): AuthRepository
}
