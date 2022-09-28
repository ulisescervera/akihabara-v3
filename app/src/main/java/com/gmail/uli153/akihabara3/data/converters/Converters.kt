package com.gmail.uli153.akihabara3.data.converters

import androidx.room.TypeConverter
import com.gmail.uli153.akihabara3.data.entities.ProductType
import java.io.File
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault())

    @TypeConverter
    fun toProductType(value: String): ProductType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromProductType(type: ProductType): String {
        return type.name
    }

    @TypeConverter
    fun toBigDecimal(value: String): BigDecimal {
        return BigDecimal(value)
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String {
        return value.toString()
    }

    @TypeConverter
    fun toDate(value: String): Date {
        return dateFormatter.parse(value)!!
    }

    @TypeConverter
    fun fromDate(value: Date): String {
        return dateFormatter.format(value)
    }

    @TypeConverter
    fun fromString(path: String): File? {
        return File(path).takeIf { it.exists() && !it.isDirectory }
    }

    @TypeConverter
    fun fromFile(file: File?): String {
        return file?.absolutePath ?: ""
    }
}
