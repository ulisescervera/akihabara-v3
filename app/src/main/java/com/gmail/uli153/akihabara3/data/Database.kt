package com.gmail.uli153.akihabara3

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gmail.uli153.akihabara3.AkihabaraDatabase.Companion.DATABASE_VERSION
import com.gmail.uli153.akihabara3.data.converters.Converters
import com.gmail.uli153.akihabara3.data.daos.ProductDao
import com.gmail.uli153.akihabara3.data.daos.TransactionDao
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.models.ProductType
import com.gmail.uli153.akihabara3.data.models.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Provider

@Database(entities = [Product::class, Transaction::class], version = DATABASE_VERSION)
@TypeConverters(Converters::class)
abstract class AkihabaraDatabase: RoomDatabase() {

    companion object {
        const val DATABASE_VERSION: Int = 2
        const val DATABASE_NAME: String = "akihabara_v2_database"

        @Volatile var instance: AkihabaraDatabase? = null
            private set

        fun buildDatabase(context: Context): AkihabaraDatabase {
            return Room.databaseBuilder(context, AkihabaraDatabase::class.java, DATABASE_NAME)
                .addCallback(AkihabaraDatabaseCallack())
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }

    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao

}

class AkihabaraDatabaseCallack(): RoomDatabase.Callback() {

    private val scope = CoroutineScope(SupervisorJob())

    private val initialProducts: List<Product> by lazy {
        val l = mutableListOf<Product>()
        for (i in 1..20) {
            if (i <= 10) {
                l.add(Product(i.toLong(), ProductType.DRINK , "bebida ${i}", BigDecimal(1.0 * i), R.drawable.ic_res_drink1, null))
            } else {
                l.add(Product(i.toLong(), ProductType.FOOD, "comida ${i}", BigDecimal(1.0 * i), R.drawable.ic_res_food6, null))
            }

        }
        l
    }

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val dao = AkihabaraDatabase.instance?.productDao() ?: return
        scope.launch(Dispatchers.IO) {
            for (p in initialProducts) {
                dao.insert(p)
            }
        }
    }


}