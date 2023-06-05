package com.gmail.uli153.akihabara3.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.ActivityMainBinding
import com.gmail.uli153.akihabara3.ui.bottomsheets.BalanceBottomSheet
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductFormViewModel
import com.gmail.uli153.akihabara3.utils.PreferenceUtils
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private val productsFormViewModel: ProductFormViewModel by viewModels()
    private val bggViewModel: BggViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceUtils = PreferenceUtils(this)
        if (preferenceUtils.getBoolean(PreferenceUtils.PreferenceKeys.FirstTimeStarted, true)) {
            preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FilterBoardgame, true)
            preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FilterBoardGameExpansion, true)
            preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FilterBoardgameAccessory, true)
            preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FilterVideogame, true)
            preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FirstTimeStarted, false)
        }

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null
        navView.background = null

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.destination_products,
                R.id.destination_bgg,
                R.id.destination_history
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.destination_products, R.id.destination_history -> {
                    binding.bottomAppBar.performShow(true)
                    binding.fav.show()
                    binding.toolbar.navigationIcon = null
//                    binding.lottieView.isGone = false
                    binding.btnActionBarBgg.isGone = false
                }
                else -> {
                    binding.bottomAppBar.performHide(true)
                    binding.fav.hide()
                    binding.toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
//                    binding.lottieView.isGone = true
                    binding.btnActionBarBgg.isGone = true
                }
            }

            binding.btnActionBarGridMode.isGone = destination.id != R.id.destination_bgg
        }

        binding.btnAddProduct.setSafeClickListener {
            productsFormViewModel.setProductFormImage(R.drawable.ic_res_drink10)
            navController.navigate(R.id.action_create_product)
        }

        binding.btnEditBalance.setSafeClickListener {
            BalanceBottomSheet.show(supportFragmentManager)
        }

//        binding.lottieView.setSafeClickListener {
////            InfoBottomSheet.show(supportFragmentManager)
//        }

        binding.btnActionBarBgg.setSafeClickListener {
            navController.navigate(R.id.action_bgg)
        }

        binding.btnActionBarGridMode.setSafeClickListener {
            bggViewModel.toggleGridMode()
        }

        bggViewModel.gridMode.observe(this) { grid ->
            val res = if (grid) R.drawable.ic_list_mode else R.drawable.ic_grid_mode
            binding.btnActionBarGridMode.setImageResource(res)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

/**
 * TODO
 * - Alert confirm shows default Material Theme colors
 * - Márgenes snackbar
 * - Imagen transacción balance
 */
