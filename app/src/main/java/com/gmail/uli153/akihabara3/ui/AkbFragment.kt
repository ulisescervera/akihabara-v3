package com.gmail.uli153.akihabara3.ui

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.gmail.uli153.akihabara3.R

open class AkbFragment: Fragment() {

    protected val navController: NavController get() {
        return findNavController()
    }

    protected fun navigate(dirs: NavDirections) {
        navController.navigate(dirs)
    }

    protected fun showConfirmDialog(
        title: String = getString(R.string.attention),
        message: String,
        cancel: DialogInterface.OnClickListener? = null,
        accept: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(requireContext(), R.style.AkbDialog)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(getString(R.string.cancel), cancel)
            .setPositiveButton(getString(R.string.accept), accept)
            .create()
            .show()
    }
}