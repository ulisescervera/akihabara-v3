package com.gmail.uli153.akihabara3.ui

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

open class AkbFragment: Fragment() {

    protected val navController: NavController get() {
        return findNavController()
    }

    protected fun navigate(dirs: NavDirections) {
        navController.navigate(dirs)
    }
}