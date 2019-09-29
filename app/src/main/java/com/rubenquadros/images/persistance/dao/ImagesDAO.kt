package com.rubenquadros.images.persistance.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rubenquadros.images.persistance.entity.ImagesEntity

@Dao
interface ImagesDAO {

    @Insert
    fun insertImage(data: ImagesEntity)

    @Query("SELECT * FROM image_data")
    fun getAllImages(): List<ImagesEntity>
}