package com.gmail.uli153.akihabara3.ui

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.ActivityMainBinding
import com.gmail.uli153.akihabara3.ui.bottomsheet.BalanceBottomSheet
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.toPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null

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
                    binding.fav.show()
                    binding.bottomAppBar.fabCradleMargin = 6.toPx
                    binding.bottomAppBar.fabCradleRoundedCornerRadius = 12.toPx
                    binding.bottomAppBar.cradleVerticalOffset = 0f
                }
                else -> {
                    binding.fav.hide()
                    binding.bottomAppBar.fabCradleMargin = 0f
                    binding.bottomAppBar.fabCradleRoundedCornerRadius = 0f
                    binding.bottomAppBar.cradleVerticalOffset = 0f
                }
            }
        }

        binding.btnAddProduct.setSafeClickListener {
            navController.navigate(R.id.destination_create_product)
        }

        binding.btnEditBalance.setSafeClickListener {
            BalanceBottomSheet.show(supportFragmentManager)
        }
    }
}

/**
 * TODO
 * - Cradle not seen ok in xiaomi mi 9 lite dark mode
 * - Alert confirm shows default Material Theme colors
 * - Crop image from gallery
 * - Camera preview
 * - Resize image from camera/gallery
 */