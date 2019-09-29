package com.rubenquadros.images.utils

import android.content.Context
import android.os.Environment.DIRECTORY_PICTURES
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ApplicationUtility {

    companion object {

        @Throws(IOException::class)
        fun createImageFile(context: Context): File {
            val timeStamp = SimpleDateFormat(ApplicationConstants.DATE_FORMAT, Locale.getDefault()).format(Date())
            val imageFileName = ApplicationConstants.IMAGE_PREFIX + timeStamp + ApplicationConstants.IMAGE_SUFFIX
            val storageDir = context.getExternalFilesDir(DIRECTORY_PICTURES)
            return File.createTempFile(imageFileName, ApplicationConstants.JPG_SUFFIX, storageDir)
        }

        fun checkDir(path: String) : Boolean {
            var result = true
            val file = File(path)
            if(!file.exists()) {
                result = file.mkdirs()
            }else if(file.isFile) {
                file.delete()
                result = file.mkdirs()
            }
            return result
        }

        fun showSnack(msg: String, view: View, action: String){
            val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction(action) {
                snackBar.dismiss()
            }
            snackBar.show()
        }

        fun showToastMsg(msg: String, context: Context) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

    }
}