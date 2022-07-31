package com.gmail.uli153.akihabara3.ui.products

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.ui.products.base.ProductFormBaseFragment
import com.gmail.uli153.akihabara3.ui.views.AkbButtonStyle
import com.gmail.uli153.akihabara3.utils.*
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.fragment_product_base_form.*
import kotlinx.coroutines.*
import java.io.File
import java.math.BigDecimal

class EditProductFragment: ProductFormBaseFragment() {

    private val args: EditProductFragmentArgs by navArgs()

    private lateinit var product: Product

    private val likeListener: OnLikeListener = object : OnLikeListener {
        override fun liked(likeButton: LikeButton?) {
            updateButton()
        }
        override fun unLiked(likeButton: LikeButton?) {
            updateButton()
        }
    }

    override fun updateImage(file: Any?) {
        when (file) {
            is File -> {
                Glide.with(binding.imageviewProduct).load(file).circleCrop().into(binding.imageviewProduct)
                binding.imageviewProduct.setPadding(0)
            }
            is Int -> {
                Glide.with(binding.imageviewProduct).load(file).into(binding.imageviewProduct)
                binding.imageviewProduct.setPadding(20.toPx.toInt())
            }
            else -> binding.imageviewProduct.setProductImage(product)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product = args.productId.let { productId ->
            productsViewModel.products.value?.let { wrapper ->
                if (wrapper is DataWrapper.Success<List<Product>>) {
                    wrapper.data.firstOrNull { it.id == productId }
                } else null
            }
        } ?: run {
            // Nunca debería darse este caso
            navController.navigateUp()
            return
        }

        updateImage(product.customImage ?: product.defaultImage)
        binding.btnFavorite.isLiked = product.favorite
        binding.btnFavorite.setOnLikeListener(likeListener)
        binding.editName.setText(product.name)
        binding.editPrice.setText(AkbNumberParser.LocaleParser.format(product.price))
        categories.indexOf(product.type).takeIf { it >= 0 }?.let {
            binding.spinnerType.setSelection(it)
        }
        binding.button.setSafeClickListener {
            saveProduct()
        }
    }

    override fun updateButton() {
        val isValid = isValidName && isValidPrice
        val image = productsFormViewModel.productFormImage.value
        val fileChanged = image != null
        val changed = type != product.type
                || name != product.name
                || isFavorite != product.favorite
                || price?.compareTo(product.price) != 0
                || fileChanged
        val enable = isValid && changed
        binding.button.style = if (enable) AkbButtonStyle.MAIN else AkbButtonStyle.GREY
    }

    private fun saveProduct() {
        val price = this.price ?: return

        val saveListener = DialogInterface.OnClickListener { _, _ ->
            productsViewModel.viewModelScope.launch(Dispatchers.Main) {
                val image = productsFormViewModel.productFormImage.value?.let {
                    if (it is File) {
                        val finalFile = newImageFile()
                        runBlocking {
                            FileUtils.moveFile(it, finalFile)
                        }
                        finalFile
                    } else {
                        it
                    }
                }
                val product = product.copy(
                    type = type,
                    name = name,
                    price = price,
                    favorite = isFavorite,
                    customImage = image as? File,
                    defaultImage = image as? Int ?: 0
                )
                productsViewModel.addProduct(product)
                navController.navigateUp()
            }
        }

        AlertDialog.Builder(requireContext(), R.style.AkbDialog)
            .setTitle(R.string.attention)
            .setMessage(R.string.save_product_changes_message)
            .setNegativeButton(getString(R.string.no), null)
            .setPositiveButton(getString(R.string.yes), saveListener)
            .show()
    }
}