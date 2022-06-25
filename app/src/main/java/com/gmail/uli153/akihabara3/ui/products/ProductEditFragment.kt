package com.gmail.uli153.akihabara3.ui.products

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.ui.products.base.ProductFormBaseFragment
import com.gmail.uli153.akihabara3.ui.views.AkbButtonStyle
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.DataWrapper
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ProductEditFragment: ProductFormBaseFragment() {

    private val args: ProductEditFragmentArgs by navArgs()

    private lateinit var product: Product

    private val name: String get() {
        return binding.editName.text.toString()
    }

    private val isValidName: Boolean get() {
        return name.isNotBlank()
    }

    private val price: BigDecimal? get() {
        return AkbNumberParser.LocaleParser.parseToBigDecimal(binding.editPrice.text.toString())
    }

    private val isValidPrice: Boolean get() {
        val price = this.price ?: return false
        return price >= BigDecimal(0)
    }

    private val isFavorite: Boolean get() {
        return binding.btnFavorite.isLiked
    }

    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun afterTextChanged(p0: Editable?) {
            updateButton()
        }
    }

    private val likeListener: OnLikeListener = object : OnLikeListener {
        override fun liked(likeButton: LikeButton?) {
            updateButton()
        }
        override fun unLiked(likeButton: LikeButton?) {
            updateButton()
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

        binding.btnFavorite.isLiked = product.favorite
        binding.btnFavorite.setOnLikeListener(likeListener)
        binding.editName.setText(product.name)
        binding.editName.addTextChangedListener(textWatcher)
        binding.editPrice.addTextChangedListener(textWatcher)
        binding.editPrice.setText(AkbNumberParser.LocaleParser.format(product.price))
        binding.button.setSafeClickListener {
            saveProduct()
        }
    }

    private fun updateButton() {
        val isValid = isValidName && isValidPrice
        val changed = name != product.name || isFavorite != product.favorite || price?.compareTo(product.price) != 0
        val enable = isValid && changed
        binding.button.style = if (enable) AkbButtonStyle.MAIN else AkbButtonStyle.GREY
    }

    private fun saveProduct() {
        val price = this.price ?: return

        val product = product.copy(name = name, price = price, favorite = isFavorite)
        val saveListener = DialogInterface.OnClickListener { dialogInterface, i ->
            productsViewModel.viewModelScope.launch(Dispatchers.Main) {
                navController.navigateUp()
                delay(500)
                productsViewModel.addProduct(product)
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