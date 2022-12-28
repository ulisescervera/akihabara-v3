package com.gmail.uli153.akihabara3.ui

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.gmail.uli153.akihabara3.R
import java.io.File
import java.net.SocketTimeoutException

abstract class AkbFragment<T: ViewBinding>: Fragment() {

    abstract fun inflateView(inflater: LayoutInflater, container: ViewGroup?): T

    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    protected val navController: NavController get() {
        return findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflateView(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected fun navigate(dirs: NavDirections) {
        navController.navigate(dirs)
    }

    protected fun navigateUp() {
        navController.navigateUp()
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

    protected fun showDialog(
        title: String = getString(R.string.attention),
        message: String,
        accept: DialogInterface.OnClickListener? = null
    ) {
        AlertDialog.Builder(requireContext(), R.style.AkbDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.accept), accept)
            .create()
            .show()
    }

    protected fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    protected fun newTmpFile(): File {
        return File(requireContext().cacheDir, "${System.currentTimeMillis()}_tmp_file")
    }

    protected fun newImageFile(): File {
        return File(requireContext().filesDir, "${System.currentTimeMillis()}_image.jpeg")
    }

    protected fun getCustomErrorMessage(error: Throwable?): String {
        return when(error) {
            is SocketTimeoutException -> getString(R.string.error_timeout)
            else -> getString(R.string.error_general)
        }
    }
}
