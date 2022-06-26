package com.gmail.uli153.akihabara3.ui.products

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.ui.products.base.ProductFormBaseFragment
import com.gmail.uli153.akihabara3.ui.views.AkbButtonStyle
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddProductFragment: ProductFormBaseFragment() {

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

        val newProduct = Product(
            type = categories[binding.spinnerType.selectedItemPosition],
            name = name,
            price = price,
            defaultImage = 0,
            localImage = "",
            favorite = isFavorite
        )

        navigateUp()
        lifecycleScope.launch {
            delay(250)
            productsViewModel.addProduct(newProduct)
        }
    }
}