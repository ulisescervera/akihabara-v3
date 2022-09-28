package com.gmail.uli153.akihabara3.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.ActivityMainBinding
import com.gmail.uli153.akihabara3.ui.bottomsheets.BalanceBottomSheet
import com.gmail.uli153.akihabara3.ui.bottomsheets.InfoBottomSheet
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductFormViewModel
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val productsFormViewModel: ProductFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null
        navView.background = null

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.destination_products,
                R.id.destination_settings,
                R.id.destination_history
            )
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.destination_products, R.id.destination_history -> {
                    binding.bottomAppBar.performShow(true)
                    binding.fav.show()
                    binding.toolbar.navigationIcon = null
                }
                else -> {
                    binding.bottomAppBar.performHide(true)
                    binding.fav.hide()
                    binding.toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
                }
            }
        }

        binding.btnAddProduct.setSafeClickListener {
            productsFormViewModel.setProductFormImage(R.drawable.ic_res_drink10)
            navController.navigate(R.id.destination_create_product)
        }

        binding.btnEditBalance.setSafeClickListener {
            BalanceBottomSheet.show(supportFragmentManager)
        }

        binding.lottieView.setSafeClickListener {
            InfoBottomSheet.show(supportFragmentManager)
        }
    }
}

/**
 * TODO
 * - Alert confirm shows default Material Theme colors
 * - Márgenes snackbar
 * - Imagen transacción balance
 */
