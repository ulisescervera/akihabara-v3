package com.gmail.uli153.akihabara3.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

sealed class AkbNumberParser(minFraction: Int, maxFraction: Int): DecimalFormat() {
    object LocaleParser: AkbNumberParser(2, 2) {
        fun parseToEur(value: BigDecimal): String {
            return String.format("%s €", format(value))
        }
    }

    init {
        minimumIntegerDigits = 1
        minimumFractionDigits = minFraction
        maximumFractionDigits = maxFraction
        decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
        isParseBigDecimal = true
    }

    fun parseToDouble(value: String): Double? {
        return try {
            parse(value)?.toDouble()
        } catch (e: Exception) {
            null
        }
    }

    fun parseToBigDecimal(value: String): BigDecimal? {
        return try {
            parse(value) as? BigDecimal
        } catch (e: Exception) {
            null
        }
    }

}
