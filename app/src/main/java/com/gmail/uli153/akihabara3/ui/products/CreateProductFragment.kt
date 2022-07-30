package com.gmail.uli153.akihabara3.ui.products

import android.os.Bundle
import android.view.View
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.ui.products.base.ProductFormBaseFragment
import com.gmail.uli153.akihabara3.ui.views.AkbButtonStyle
import com.gmail.uli153.akihabara3.utils.setProductImage
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.toPx
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class CreateProductFragment: ProductFormBaseFragment() {

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
            else -> binding.imageviewProduct.setImageDrawable(null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setSafeClickListener {
            trySaveProduct()
        }
    }

    override fun updateButton() {
        val isValid = isValidName && isValidPrice
        binding.button.style = if (isValid) AkbButtonStyle.MAIN else AkbButtonStyle.GREY
    }

    private fun trySaveProduct() {
        val name = this.name.takeIf { isValidName } ?: run {
            showDialog(message = getString(R.string.set_valid_name))
            return
        }

        val price = this.price?.takeIf { isValidPrice } ?: run {
            showDialog(message = getString(R.string.set_valid_price))
            return
        }

        val img = productsViewModel.productFormImage.value
        val newProduct = Product(
            type = categories[binding.spinnerType.selectedItemPosition],
            name = name,
            price = price,
            defaultImage = img as? Int ?: 0,
            customImage = img as? File,
            favorite = isFavorite
        )

        navigateUp()
        productsViewModel.addProduct(newProduct)
    }
}