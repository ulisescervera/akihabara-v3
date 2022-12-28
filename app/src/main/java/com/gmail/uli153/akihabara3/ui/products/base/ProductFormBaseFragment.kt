package com.gmail.uli153.akihabara3.ui.products.base

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.entities.ProductType
import com.gmail.uli153.akihabara3.databinding.FragmentProductBaseFormBinding
import com.gmail.uli153.akihabara3.databinding.FragmentProductsBaseBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.bottomsheets.ImagesBottomSheet
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductFormViewModel
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.math.BigDecimal

abstract class ProductFormBaseFragment: AkbFragment<FragmentProductBaseFormBinding>(), ImagesBottomSheet.ImageSelectedListener {

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentProductBaseFormBinding {
        return FragmentProductBaseFormBinding.inflate(inflater, container, false)
    }

    abstract fun updateButton()
    abstract fun updateImage(file: Any?)

    protected val productsViewModel: ProductsViewModel by activityViewModels()
    protected val productsFormViewModel: ProductFormViewModel by activityViewModels()

    protected val type: ProductType get() {
        return categories[binding.spinnerType.selectedItemPosition]
    }

    protected val name: String get() {
        return binding.editName.text.toString()
    }

    protected val isValidName: Boolean get() {
        return name.isNotBlank()
    }

    protected val price: BigDecimal? get() {
        return AkbNumberParser.LocaleParser.parseToBigDecimal(binding.editPrice.text.toString())
    }

    protected val isValidPrice: Boolean get() {
        val price = this.price ?: return false
        return price >= BigDecimal(0)
    }

    protected val isFavorite: Boolean get() {
        return binding.btnFavorite.isLiked
    }

    protected val categories: Array<ProductType> = ProductType.values()

    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun afterTextChanged(p0: Editable?) {
            updateButton()
        }
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            updateButton()
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            updateButton()
        }
    }

    private val adapter by lazy {
        ArrayAdapter(requireContext(), R.layout.spinner_row, R.id.label, categories.map { getString(it.nameResId) })
    }

    private val easy by lazy {
        EasyImage.Builder(requireContext())
            .allowMultiple(false)
            .setChooserTitle(getString(R.string.choose_image))
            .build()
    }

    private lateinit var launcher: ActivityResultLauncher<String>

    override fun onImageSelected(imageRes: Int?) {
        productsFormViewModel.setProductFormImage(imageRes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) return@registerForActivityResult

            navController.navigate(R.id.action_open_camera)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editName.addTextChangedListener(textWatcher)
        binding.editPrice.addTextChangedListener(textWatcher)
        binding.spinnerType.adapter = adapter
        binding.spinnerType.onItemSelectedListener = spinnerListener
        binding.imageviewProduct.setSafeClickListener {
            showImagesBottomSheet()
        }
        productsFormViewModel.productFormImage.observe(viewLifecycleOwner) {
            updateImage(it)
            updateButton()
        }
    }

    protected fun showImagesBottomSheet() {
        ImagesBottomSheet.show(childFragmentManager, this)
    }

    override fun showCamera() {
        if (isPermissionGranted(Manifest.permission.CAMERA)) {
            navController.navigate(R.id.action_open_camera)
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun showGallery() {
        easy.openGallery(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easy.handleActivityResult(requestCode, resultCode, data, requireActivity(), object : EasyImage.Callbacks {
            override fun onCanceled(source: MediaSource) {}
            override fun onImagePickerError(error: Throwable, source: MediaSource) {}

            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                imageFiles.firstOrNull()?.file?.let { file ->
                    productsFormViewModel.setGalleryImage(file)
                    navController.navigate(R.id.action_crop_image)
                }
            }
        })
    }

}
