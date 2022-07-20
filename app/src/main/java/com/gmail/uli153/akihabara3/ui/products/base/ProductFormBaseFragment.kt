package com.gmail.uli153.akihabara3.ui.products.base

import android.content.Intent
import android.net.Uri
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
import androidx.lifecycle.lifecycleScope
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.ProductType
import com.gmail.uli153.akihabara3.databinding.FragmentProductBaseFormBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.bottomsheet.ImagesBottomSheet
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import kotlinx.android.synthetic.main.fragment_product_base_form.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.io.*
import java.math.BigDecimal

abstract class ProductFormBaseFragment: AkbFragment(), ImagesBottomSheet.ImageSelectedListener {

    abstract fun updateButton()
    abstract fun updateImage(file: Any?)

    protected lateinit var binding: FragmentProductBaseFormBinding
    protected val productsViewModel: ProductsViewModel by activityViewModels()

    protected var image: Any? = null
        set(value) {
            field = value
            updateImage(value)
            updateButton()
        }

    protected val type: ProductType get() {
        return categories[spinner_type.selectedItemPosition]
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

    private val tmpFile: File by lazy {
        File(requireContext().filesDir, "image_jpeg")
    }

    override fun onImageSelected(imageRes: Int?) {
        this.image = imageRes
        updateImage(imageRes)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProductBaseFormBinding.inflate(inflater, container, false)
        return binding.root
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
    }

    protected fun showImagesBottomSheet() {
        ImagesBottomSheet.show(childFragmentManager, this)
    }

    override fun showCamera() {
        easy.openCameraForImage(this)
    }

    override fun showGallery() {
        easy.openGallery(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easy.handleActivityResult(requestCode, resultCode, data, requireActivity(), object : EasyImage.Callbacks {
            override fun onCanceled(source: MediaSource) {
            }
            override fun onImagePickerError(error: Throwable, source: MediaSource) {
            }

            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                imageFiles.firstOrNull()?.file?.let { file ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        moveFile(file, tmpFile)
                        image = tmpFile
                    }
                }
            }
        })
    }

    private suspend fun moveFile(inputFile: File, outputFile: File) = withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null
        var out: OutputStream? = null
        try {
            //create output directory if it doesn't exist
            if (!outputFile.exists()) {
                outputFile.createNewFile()
            }
            inputStream = FileInputStream(inputFile)
            out = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            inputStream.close()
            inputStream = null

            // write the output file
            out.flush()
            out.close()
            out = null

            // delete the original file
            inputFile.delete()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}