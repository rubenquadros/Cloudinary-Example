package com.rubenquadros.images.di.module

import com.rubenquadros.images.persistance.dao.ImagesDAO
import com.rubenquadros.images.persistance.repository.ImagesRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun providePlacesRepository(placesDAO: ImagesDAO): ImagesRepository {
        return ImagesRepository(placesDAO)
    }
}