package com.rubenquadros.images.di.module

import android.app.Application
import androidx.room.Room
import com.rubenquadros.images.persistance.dao.ImagesDAO
import com.rubenquadros.images.persistance.database.ImagesDB
import dagger.Module
import dagger.Provides


@Module
class DbModule(private val application: Application) {
    @Provides
    fun providePlacesDatabase(): ImagesDB {
        return Room.databaseBuilder(application, ImagesDB::class.java, "Images.db")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providePlacesDAO(imagesDatabase: ImagesDB): ImagesDAO {
        return imagesDatabase.imagesDAO()
    }
}