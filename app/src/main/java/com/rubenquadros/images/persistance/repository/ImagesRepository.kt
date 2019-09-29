package com.rubenquadros.images.persistance.repository

import androidx.lifecycle.MutableLiveData
import com.rubenquadros.images.persistance.dao.ImagesDAO
import com.rubenquadros.images.persistance.dbCallback.IDBCallback
import com.rubenquadros.images.persistance.entity.ImagesEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ImagesRepository(private val imagesDAO: ImagesDAO) {

    private var imagesData: List<ImagesEntity> = ArrayList()
    private var images: MutableLiveData<List<ImagesEntity>> = MutableLiveData()

    fun insertImage(imagesEntity: ImagesEntity) {
        doAsync {
            imagesDAO.insertImage(imagesEntity)
        }
    }

    fun fetchAllImages(): MutableLiveData<List<ImagesEntity>> {
        setImages(object : IDBCallback {
            override fun onQueryExecuted(imagesData: List<ImagesEntity>) {
                images.value = imagesData
            }
        })
        return images
    }

    private fun setImages(callback: IDBCallback) {
        doAsync {
            imagesData = imagesDAO.getAllImages()
            uiThread { callback.onQueryExecuted(imagesData) }
        }
    }
}