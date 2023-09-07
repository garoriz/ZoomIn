package com.garif.zoomin.feature.magnifyingglass.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.garif.zoomin.R
import com.garif.zoomin.YUVtoRGB
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException


const val PERMISSION_REQUEST_CAMERA = 83854

class MainFragment : Fragment(R.layout.fragment_magnifyong_glass) {
    private var preview: ImageView? = null
    var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    val translator = YUVtoRGB()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preview = view.findViewById(R.id.preview)
        initializeCamera()
    }

    private fun initializeCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture?.addListener({
            try {

                val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1024, 768))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(requireContext())
                ) {
                    val img = it.image
                    val bitmap =
                        img?.let { it1 -> translator.translateYUV(it1, requireActivity()) }
                    preview?.rotation = it.imageInfo.rotationDegrees.toFloat()
                    preview?.setImageBitmap(bitmap)
                    it.close()
                }

                cameraProviderFuture?.get()
                    ?.bindToLifecycle(viewLifecycleOwner, cameraSelector, imageAnalysis)
                    ?.cameraControl?.setLinearZoom(0.7f)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
}