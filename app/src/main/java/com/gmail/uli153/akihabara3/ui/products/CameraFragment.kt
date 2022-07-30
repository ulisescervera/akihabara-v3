package com.gmail.uli153.akihabara3.ui.products

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gmail.uli153.akihabara3.databinding.FragmentCameraBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.toPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CameraFragment: AkbFragment() {

    private lateinit var binding: FragmentCameraBinding

    private val productsViewModel: ProductsViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCamera()
        binding.btnTakePhoto.setSafeClickListener {
            val imageCapture = this.imageCapture ?: return@setSafeClickListener

            val options = ImageCapture.OutputFileOptions.Builder(newTmpFile()).build()
            val executor = ContextCompat.getMainExecutor(requireContext())
            val callback = object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    outputFileResults.savedUri?.let {
                        lifecycleScope.launch(Dispatchers.Main) {
                            val imageFile = cropSquare(it.toFile().absolutePath)
                            productsViewModel.setProductFormImage(imageFile)
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

    private suspend fun cropSquare(
        path: String,
        croppedSize: Int = 100.toPx.toInt()
    ): File = withContext(Dispatchers.Default) {
        val bitmap = BitmapFactory.decodeFile(path)
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size)/2
        val y = (bitmap.height - size)/2
        val cropped = Bitmap.createBitmap(bitmap, x, y, size, size)
        val scaled = Bitmap.createScaledBitmap(cropped, croppedSize, croppedSize, true)


        val oldExif = ExifInterface(path)
        val exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)
        val b: Bitmap
        if (exifOrientation != null) {
//            val newExif = ExifInterface(file.absolutePath)
//            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
//            newExif.saveAttributes()
            b = rotateBitmap(scaled, oldExif)
        } else {
            b = scaled
        }

        val file = newTmpFile()
        saveBitmap(b, file)

        return@withContext file
    }

    private suspend fun saveBitmap(bitmap: Bitmap, file: File) = withContext(Dispatchers.IO) {
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private suspend fun rotateBitmap(bitmap: Bitmap, exif: ExifInterface): Bitmap = withContext(Dispatchers.Default) {
        var rotate = 0f
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270f
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180f
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90f
        }
        val matrix = Matrix()
        matrix.postRotate(rotate)
        return@withContext Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}