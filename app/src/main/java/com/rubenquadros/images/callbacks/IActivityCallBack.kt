package com.rubenquadros.images.callbacks

interface IActivityCallBack {

    fun showDialog()

    fun showProgress(shouldShow: Boolean)

    fun onTaskCompleted(status: String, publicID: String)

    fun onImagesFetched(imageUrls: ArrayList<String>)

}