package com.gmail.uli153.akihabara3.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageView
import com.gmail.uli153.akihabara3.databinding.FragmentCropBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductFormViewModel
import com.gmail.uli153.akihabara3.utils.FileUtils
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.extensions.toPx
import kotlinx.coroutines.launch

class CropFragment: AkbFragment() {

    private val productFormViewModel: ProductFormViewModel by activityViewModels()

    private var _binding: FragmentCropBinding? = null
    private val binding: FragmentCropBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCropBinding.inflate(inflater, container, false).apply {
            _binding = this
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageSize = 120.toPx.toInt()
        binding.cropImageview.cropShape = CropImageView.CropShape.OVAL
        binding.cropImageview.setAspectRatio(1,1)
        binding.cropImageview.setMinCropResultSize(imageSize, imageSize)

        binding.btnRotateLeft.setOnClickListener {
            binding.cropImageview.rotateImage(-90)
        }
        binding.btnCrop.setSafeClickListener {
            lifecycleScope.launch {
                val bitmap = binding.cropImageview.getCroppedImage(imageSize, imageSize)
                if (bitmap != null) {
                    val file = newTmpFile()
                    FileUtils.saveBitmap(bitmap, file)
                    productFormViewModel.setProductFormImage(file)
                    navigateUp()
                }
            }
        }
        binding.btnRotateRight.setOnClickListener {
            binding.cropImageview.rotateImage(90)
        }

        productFormViewModel.galleryImageFile.observe(viewLifecycleOwner) {
            it?.toUri()?.let {
                binding.cropImageview.setImageUriAsync(it)
            }
        }
    }
}
