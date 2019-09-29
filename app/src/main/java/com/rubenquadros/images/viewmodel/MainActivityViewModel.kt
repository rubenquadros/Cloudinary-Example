package com.rubenquadros.images.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rubenquadros.images.callbacks.IActivityCallBack
import com.rubenquadros.images.persistance.entity.ImagesEntity
import com.rubenquadros.images.persistance.repository.ImagesRepository
import com.rubenquadros.images.service.CloudinaryService
import javax.inject.Inject

class MainActivityViewModel @Inject
constructor(private val imagesRepository: ImagesRepository): ViewModel()
{
    private lateinit var mActivityCallBack: IActivityCallBack
    private lateinit var cloudinaryService: CloudinaryService
    private var dbResponse: MutableLiveData<List<ImagesEntity>> = MutableLiveData()
    private var imagesList: ArrayList<String> = ArrayList()

    fun fabClicked(view: View) {
        mActivityCallBack.showDialog()
    }

    fun uploadImage(filepath: String, publicID: String) {
        cloudinaryService = CloudinaryService()
        cloudinaryService.setListener(mActivityCallBack)
        cloudinaryService.uploadImage(filepath, publicID)
    }

    fun insertImage(imageURL: String) {
        val imagesEntity = ImagesEntity(imageUrl = imageURL, id = 0)
        imagesRepository.insertImage(imagesEntity)
    }

    fun initLocalData() {
        dbResponse = imagesRepository.fetchAllImages()
    }

    fun fetchImages(): LiveData<List<ImagesEntity>> {
        return dbResponse
    }

    fun fetchImagesWithRes(height: Int, width: Int) {
        cloudinaryService = CloudinaryService()
        initLocalData()
        imagesList.removeAll(imagesList)
        for(i in dbResponse.value!!.indices) {
            imagesList.add(cloudinaryService.fetchImages(height, width, dbResponse.value!![i].imageUrl))
        }
        mActivityCallBack.onImagesFetched(imagesList)
    }

    fun setListener(activityCallBack: IActivityCallBack) {
        mActivityCallBack = activityCallBack
    }
}