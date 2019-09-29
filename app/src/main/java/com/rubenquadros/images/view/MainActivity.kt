package com.rubenquadros.images.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.cloudinary.android.MediaManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rubenquadros.images.BuildConfig
import com.rubenquadros.images.R
import com.rubenquadros.images.adapter.RecViewAdapter
import com.rubenquadros.images.base.BaseActivity
import com.rubenquadros.images.callbacks.IActivityCallBack
import com.rubenquadros.images.databinding.ActivityMainBinding
import com.rubenquadros.images.persistance.entity.ImagesEntity
import com.rubenquadros.images.utils.ApplicationConstants
import com.rubenquadros.images.utils.ApplicationUtility
import com.rubenquadros.images.viewmodel.MainActivityViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("PrivatePropertyName", "UNUSED_PARAMETER")
class MainActivity : BaseActivity(), IActivityCallBack, AdapterView.OnItemSelectedListener {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var alertDialog: AlertDialog
    private lateinit var gallery: TextView
    private lateinit var camera: TextView
    private lateinit var cancel: TextView
    private lateinit var photoFile: File
    private val ACCESS_GALLERY = 10211
    private val ACCESS_CAMERA = 10210
    private val GALLERY_RC = 10001
    private val CAMERA_RC = 10201
    private lateinit var DIR: String
    private var imagesList = ArrayList<String>()
    private var allow = false

    @BindView(R.id.parent) lateinit var mView: ConstraintLayout
    @BindView(R.id.progressBar) lateinit var mProgressBar: ProgressBar
    @BindView(R.id.addButton) lateinit var addButton: FloatingActionButton
    @BindView(R.id.recyclerView) lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.spinner) lateinit var mSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        init()
        setupRecView()
        fetchImages()
        setupSpinner()
    }

    private fun setupBinding() {
        val activityBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory)[MainActivityViewModel::class.java]
        activityBinding.handler = mainActivityViewModel
        ButterKnife.bind(this)
    }

    private fun init() {
        MediaManager.init(this)
        mainActivityViewModel.setListener(this)
        DIR = Environment.getExternalStorageDirectory().absolutePath + ApplicationConstants.MAIN_DIR
    }

    private fun setupRecView() {
        val layoutManager = GridLayoutManager(this, 3)
        mRecyclerView.layoutManager = layoutManager
    }

    private fun setupSpinner() {
        mSpinner.onItemSelectedListener = this
    }

    private fun fetchImages() {
        showProgress(true)
        mainActivityViewModel.initLocalData()
        mainActivityViewModel.fetchImages().observe(this, Observer<List<ImagesEntity>> { t->
            if(t != null && t.isNotEmpty()) {
                showProgress(false)
                allow = true
                //updateUI(t)
            }else {
                showProgress(false)
            }
        })
    }

    private fun updateUI(images: List<ImagesEntity>) {
        for(i in images.indices) {
            imagesList.add(images[i].imageUrl!!)
        }
        val adapter = RecViewAdapter(imagesList)
        mRecyclerView.adapter = adapter
    }

    @SuppressLint("InflateParams")
    override fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.cusotm_dialog, null, false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_title))
        builder.setView(dialogView)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        camera = alertDialog.findViewById(R.id.camera)
        gallery = alertDialog.findViewById(R.id.gallery)
        cancel = alertDialog.findViewById(R.id.cancel)
        camera.setOnClickListener {
            alertDialog.dismiss()
            this.requestPermAndProcess(ApplicationConstants.CAMERA_PERMISSION) }
        gallery.setOnClickListener {
            alertDialog.dismiss()
            this.requestPermAndProcess(ApplicationConstants.GALLERY_PERMISSION) }
        cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun dispatchGalleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_RC)
    }

    private fun dispatchCameraIntent() {
        try {
            photoFile = ApplicationUtility.createImageFile(this)
        } catch (ex: IOException) {
            ApplicationUtility.showSnack(this.getString(R.string.generic_err), mView, this.getString(
                R.string.ok
            ))
            return
        }
        val photoURI =
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ApplicationConstants.PROVIDER, photoFile)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(cameraIntent, CAMERA_RC)
    }

    private fun requestPermAndProcess(permission: String) {
        when (permission) {
            ApplicationConstants.GALLERY_PERMISSION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), ACCESS_GALLERY
                    )
                } else {
                    ApplicationUtility.checkDir(DIR)
                    dispatchGalleryIntent()
                }
            }
            ApplicationConstants.CAMERA_PERMISSION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        ACCESS_CAMERA
                    )
                } else {
                    ApplicationUtility.checkDir(DIR)
                    dispatchCameraIntent()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACCESS_GALLERY -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ApplicationUtility.showSnack(
                        this.getString(R.string.gallery_permission),
                        mView,
                        this.getString(R.string.ok)
                    )
                } else {
                    ApplicationUtility.checkDir(DIR)
                    dispatchGalleryIntent()
                }
            }
            ACCESS_CAMERA -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ApplicationUtility.showSnack(
                        this.getString(R.string.camera_permission),
                        mView,
                        this.getString(R.string.ok)
                    )
                } else {
                    ApplicationUtility.checkDir(DIR)
                    dispatchCameraIntent()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_RC -> {
                    if (data != null) {
                        val mURI = data.data
                        if (mURI != null) {
                            val c = contentResolver.query(mURI, arrayOf("_data"), null, null, null)
                            if (c == null || !c.moveToFirst()) {
                                return
                            }
                            val mSourceImagePath = c.getString(0)
                            c.close()
                            mainActivityViewModel.uploadImage(mSourceImagePath, generatePublicID())
                        }
                    }
                }
                CAMERA_RC -> {
                    mainActivityViewModel.uploadImage(photoFile.absolutePath, generatePublicID())
                }
            }
        } else {
            ApplicationUtility.showToastMsg(this.getString(R.string.not_selected), this)
        }
    }

    private fun generatePublicID(): String {
        val timeStamp = SimpleDateFormat(ApplicationConstants.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        return ApplicationConstants.IMAGE_PREFIX + timeStamp
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(allow) {
            when (position) {
                0 -> {
                    this.showProgress(true)
                    mainActivityViewModel.fetchImagesWithRes(300, 400)
                }
                1 -> {
                    this.showProgress(true)
                    mainActivityViewModel.fetchImagesWithRes(30, 30)
                }
                2 -> {
                    this.showProgress(true)
                    mainActivityViewModel.fetchImagesWithRes(300, 400)
                }
                3 -> {
                    this.showProgress(true)
                    mainActivityViewModel.fetchImagesWithRes(600, 800)
                }
            }
        }
    }


    override fun showProgress(shouldShow: Boolean) {
        if(shouldShow) {
            mProgressBar.visibility = View.VISIBLE
            addButton.isEnabled = false
            addButton.isClickable = false
        }else {
            mProgressBar.visibility = View.GONE
            addButton.isEnabled = true
            addButton.isClickable = true
        }
    }

    override fun onTaskCompleted(status: String, publicID: String) {
        when(status) {
            ApplicationConstants.SUCCESS -> {
                imagesList.add(ApplicationConstants.BASE_IMG_URL + publicID)
                val adapter = RecViewAdapter(imagesList)
                mRecyclerView.adapter = adapter
                mainActivityViewModel.insertImage(ApplicationConstants.BASE_IMG_URL + publicID)
            }
            ApplicationConstants.ERROR -> {
                ApplicationUtility.showSnack(this.getString(R.string.generic_err), mView, this.getString(
                    R.string.ok))
                return
            }
        }
    }

    override fun onImagesFetched(imageUrls: ArrayList<String>) {
        imagesList = imageUrls
        this.showProgress(false)
        val adapter = RecViewAdapter(imagesList)
        mRecyclerView.adapter = adapter
    }
}
