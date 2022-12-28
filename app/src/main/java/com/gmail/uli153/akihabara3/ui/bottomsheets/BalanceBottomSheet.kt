package com.gmail.uli153.akihabara3.ui.bottomsheets

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.BottomSheetBalanceBinding
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.ui.views.AkbButtonStyle
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.math.BigDecimal
import java.text.DecimalFormat

class BalanceBottomSheet private constructor(): BottomSheetDialogFragment() {

    companion object {
        fun show(manager: FragmentManager) {
            BalanceBottomSheet().show(manager, "BalanceBottomSheet")
        }
    }

    private val productsViewModel: ProductsViewModel by activityViewModels()

    private lateinit var behavior: BottomSheetBehavior<View>

    private val SEPARATOR: String = "."

    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {
            binding.btnSaveBalance.style = if(isValidAmount) AkbButtonStyle.MAIN else AkbButtonStyle.GREY
            updateBalance()
        }
    }

    private val text: String get() {
        return binding.editBalance.text.toString()
    }

    private var balance: BigDecimal = BigDecimal(0)

    private val amount: BigDecimal get() {
        return text.replace(",", ".").toDoubleOrNull()?.toBigDecimal() ?: BigDecimal(0)
    }

    private val isValidAmount: Boolean get() {
        return amount.compareTo(BigDecimal(0)) != 0
    }

    private val formatter by lazy {
        DecimalFormat("#.##")
    }

    override fun getTheme(): Int {
        return R.style.AkbBottomSheetDialog
    }

    private var _binding: BottomSheetBalanceBinding? = null
    private val binding: BottomSheetBalanceBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.skipCollapsed = true

        binding.editBalance.addTextChangedListener(textWatcher)
        binding.btn1.setOnClickListener {
            addCharacter("1")
        }
        binding.btn2.setOnClickListener {
            addCharacter("2")
        }
        binding.btn3.setOnClickListener {
            addCharacter("3")
        }
        binding.btn4.setOnClickListener {
            addCharacter("4")
        }
        binding.btn5.setOnClickListener {
            addCharacter("5")
        }
        binding.btn6.setOnClickListener {
            addCharacter("6")
        }
        binding.btn7.setOnClickListener {
            addCharacter("7")
        }
        binding.btn8.setOnClickListener {
            addCharacter("8")
        }
        binding.btn0.setOnClickListener {
            addCharacter("9")
        }
        binding.btn0.setOnClickListener {
            addCharacter("0")
        }
        binding.btnDot.setOnClickListener {
            addCharacter(SEPARATOR)
        }
        binding.btnSign.setOnClickListener {
            toggleSign()
        }
        binding.btnRemove.setOnClickListener {
            removeLast()
        }
        binding.btnSaveBalance.setSafeClickListener {
            val amount = this.amount
            if (isValidAmount && amount.compareTo(BigDecimal(0)) != 0) {
                val decimalFormat = DecimalFormat("+0.00€")
                productsViewModel.addBalance(amount, getString(R.string.balance_title, decimalFormat.format(amount)))
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        productsViewModel.balance.observe(viewLifecycleOwner) {
            this.balance = it
            updateBalance()
        }
    }

    private fun updateBalance() {
        val a = balance.plus(amount)
        binding.labelBalanceValue.text = AkbNumberParser.LocaleParser.parseToEur(a)
    }

    private fun toggleSign() {
        if (text.isEmpty() || amount.compareTo(BigDecimal(0)) == 0) {
            binding.editBalance.setText("-")
        } else {
            val a = amount.multiply(BigDecimal(-1))
            binding.editBalance.setText(formatter.format(a))
        }

        val icon = if (amount.compareTo(BigDecimal(0)) >= 0) R.drawable.ic_sign_minus else R.drawable.ic_sign_add
        binding.btnSign.setImageResource(icon)
    }

    private fun removeLast() {
        val t = text.takeIf { it.length > 0 }?.let {
            text.substring(0, it.length - 1)
        }
        binding.editBalance.setText(t)
    }

    private fun addCharacter(c: String) {
        if (c == SEPARATOR && text.isEmpty()) return
        if (text.contains(SEPARATOR)) {
            if (c == SEPARATOR) return

            val index = text.indexOf(SEPARATOR)
            if (text.length > index + 2) return
        }

        binding.editBalance.setText(text + c)
    }

}
