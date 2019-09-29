package com.rubenquadros.images.service;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.rubenquadros.images.callbacks.IActivityCallBack;
import com.rubenquadros.images.utils.ApplicationConstants;

import java.util.Map;

public class CloudinaryService {

    private IActivityCallBack activityCallBack;

    public void uploadImage(String filePath, String publicID) {
        MediaManager.get().upload(filePath)
                .unsigned("ruben_preset")
                .option("resource_type", "auto")
                .option("public_id", publicID)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        activityCallBack.showProgress(true);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        activityCallBack.showProgress(true);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        activityCallBack.showProgress(false);
                        activityCallBack.onTaskCompleted(ApplicationConstants.SUCCESS, publicID);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        activityCallBack.showProgress(false);
                        activityCallBack.onTaskCompleted(ApplicationConstants.ERROR, publicID);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                })
                .dispatch();
    }

    public String fetchImages(int height, int width, String url) {
        return MediaManager.get().url()
                .transformation(new Transformation().width(width).height(height).fetchFormat("auto"))
                .type("fetch").generate(url);
    }

    public void setListener(IActivityCallBack callBack) {
        activityCallBack = callBack;
    }
}
