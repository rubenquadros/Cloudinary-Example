package com.rubenquadros.images.persistance.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rubenquadros.images.persistance.dao.ImagesDAO
import com.rubenquadros.images.persistance.entity.ImagesEntity

@Database(entities = [ImagesEntity::class], version = 1)
abstract class ImagesDB: RoomDatabase() {

    abstract fun imagesDAO(): ImagesDAO
}