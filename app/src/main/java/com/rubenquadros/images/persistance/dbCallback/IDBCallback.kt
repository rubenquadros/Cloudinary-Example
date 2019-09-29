package com.rubenquadros.images.persistance.dbCallback

import com.rubenquadros.images.persistance.entity.ImagesEntity

interface IDBCallback {
    fun onQueryExecuted(imagesData: List<ImagesEntity>)
}