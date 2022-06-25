package com.gmail.uli153.akihabara3.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.ActivityMainBinding
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
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
        navView.background = null
        navView.menu.getItem(1).isEnabled = false

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
                    binding.fab.isGone = false
                    binding.bottomAppBar.fabCradleMargin = 6.toPx
                    binding.bottomAppBar.fabCradleRoundedCornerRadius = 12.toPx
                    binding.bottomAppBar.cradleVerticalOffset = 0f
                }
                else -> {
                    binding.fab.isGone = true
                    binding.bottomAppBar.fabCradleMargin = 0f
                    binding.bottomAppBar.fabCradleRoundedCornerRadius = 0f
                    binding.bottomAppBar.cradleVerticalOffset = 0f
                }
            }
        }
    }
}