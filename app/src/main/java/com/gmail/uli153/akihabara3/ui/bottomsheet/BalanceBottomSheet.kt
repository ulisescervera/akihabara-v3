package com.gmail.uli153.akihabara3.ui.bottomsheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.ui.views.AkbButtonStyle
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_balance.*
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat

class BalanceBottomSheet: BottomSheetDialogFragment() {

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
            btn_save_balance.style = if(isValidAmount) AkbButtonStyle.MAIN else AkbButtonStyle.GREY
            updateBalance()
        }
    }

    private val text: String get() {
        return edit_balance.text.toString()
    }

    private var balance: BigDecimal = BigDecimal(0)

    private val amount: BigDecimal get() {
        return text.toDoubleOrNull()?.toBigDecimal() ?: BigDecimal(0)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_balance, container, false)
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.skipCollapsed = true

        edit_balance.addTextChangedListener(textWatcher)
        btn_1.setOnClickListener {
            addCharacter("1")
        }
        btn_2.setOnClickListener {
            addCharacter("2")
        }
        btn_3.setOnClickListener {
            addCharacter("3")
        }
        btn_4.setOnClickListener {
            addCharacter("4")
        }
        btn_5.setOnClickListener {
            addCharacter("5")
        }
        btn_6.setOnClickListener {
            addCharacter("6")
        }
        btn_7.setOnClickListener {
            addCharacter("7")
        }
        btn_8.setOnClickListener {
            addCharacter("8")
        }
        btn_9.setOnClickListener {
            addCharacter("9")
        }
        btn_0.setOnClickListener {
            addCharacter("0")
        }
        btn_dot.setOnClickListener {
            addCharacter(SEPARATOR)
        }
        btn_sign.setOnClickListener {
            toggleSign()
        }
        btn_remove.setOnClickListener {
            removeLast()
        }
        btn_save_balance.setSafeClickListener {
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
        label_balance_value.text = AkbNumberParser.LocaleParser.parseToEur(a)
    }

    private fun toggleSign() {
        if (text.isEmpty()) {
            edit_balance.setText("-")
        } else if (amount.compareTo(BigDecimal(0)) == 0) {
            edit_balance.setText("-0")
        } else {
            val a = amount.multiply(BigDecimal(-1))
            edit_balance.setText(formatter.format(a))
        }

        val icon = if (amount.compareTo(BigDecimal(0)) >= 0) R.drawable.ic_sign_minus else R.drawable.ic_sign_add
        btn_sign.setImageResource(icon)
    }

    private fun removeLast() {
        val t = text.takeIf { it.length > 0 }?.let {
            text.substring(0, it.length - 1)
        }
        edit_balance.setText(t)
    }

    private fun addCharacter(c: String) {
        if (c == SEPARATOR && text.isEmpty()) return
        if (text.contains(SEPARATOR)) {
            if (c == SEPARATOR) return

            val index = text.indexOf(SEPARATOR)
            if (text.length > index + 2) return
        }

        edit_balance.setText(text + c)
    }

}