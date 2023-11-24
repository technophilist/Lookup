package com.example.lookup.ui.utils

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat

/**
 * A convenience wrapper over [LifecycleCameraController.takePicture] and its other overloads.
 */
fun LifecycleCameraController.takePicture(
    context: Context,
    onSuccess: (ImageProxy) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    this.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onError(exception: ImageCaptureException) = onError(exception)
            override fun onCaptureSuccess(image: ImageProxy) = onSuccess(image)
        }
    )
}