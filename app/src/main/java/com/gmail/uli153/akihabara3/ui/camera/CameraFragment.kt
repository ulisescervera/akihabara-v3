package com.gmail.uli153.akihabara3.ui.camera

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentBggSearchBinding
import com.gmail.uli153.akihabara3.databinding.FragmentCameraBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductFormViewModel
import com.gmail.uli153.akihabara3.utils.FileUtils
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CameraFragment: AkbFragment<FragmentCameraBinding>() {

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentCameraBinding {
        return FragmentCameraBinding.inflate(inflater, container, false)
    }

    private val productsFormViewModel: ProductFormViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCamera()
        binding.btnTakePhoto.setSafeClickListener {
            onTakeImageClicked()
        }
        setupButtonAnimation()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupButtonAnimation() {
        binding.btnTakePhoto.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    AnimatorInflater.loadAnimator(requireContext(), R.animator.grows).apply {
                        setTarget(view)
                        start()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    AnimatorInflater.loadAnimator(requireContext(), R.animator.regain_size).apply {
                        setTarget(view)
                        start()
                    }
                }
            }
            false
        }
    }

    private fun onTakeImageClicked() {
        val imageCapture = this.imageCapture ?: return

        val options = ImageCapture.OutputFileOptions.Builder(newTmpFile()).build()
        val executor = ContextCompat.getMainExecutor(requireContext())
        val callback = object: ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val imageFile = FileUtils.cropSquare(it.toFile().absolutePath, newTmpFile())
                        productsFormViewModel.setProductFormImage(imageFile)
                        navigateUp()
                    }
                } ?: run {
                    //todo
                    navigateUp()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                //todo
            }
        }

        imageCapture.takePicture(options, executor, callback)
    }

    private fun setupCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(requireContext())
        val executor = ContextCompat.getMainExecutor(requireContext())
        val listener: () -> Unit = {
            val provider = providerFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.preview.surfaceProvider)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            imageCapture = ImageCapture.Builder().build()
            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        providerFuture.addListener(listener, executor)
    }

}
