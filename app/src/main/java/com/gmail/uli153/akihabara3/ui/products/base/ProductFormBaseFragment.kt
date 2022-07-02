package com.gmail.uli153.akihabara3.ui.products.base

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.models.ProductType
import com.gmail.uli153.akihabara3.databinding.FragmentProductBaseFormBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import kotlinx.android.synthetic.main.fragment_product_base_form.*
import java.math.BigDecimal

abstract class ProductFormBaseFragment: AkbFragment() {

    abstract fun updateButton()

    protected lateinit var binding: FragmentProductBaseFormBinding
    protected val productsViewModel: ProductsViewModel by activityViewModels()

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
    }

}