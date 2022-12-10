package com.shidqi.movieassestment.di

import com.shidqi.movieassestment.repository.MovieRepository
import com.shidqi.movieassestment.service.IRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Repository module instantiate as singleton so it will live as long the app running
     * **/
    @Singleton
    @Provides
    fun provideMovieRepository(retrofitService: IRetrofit,): MovieRepository {
        return MovieRepository(retrofitService)
    }
}