package com.gmail.uli153.akihabara3.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.gmail.uli153.akihabara3.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoBottomSheet private constructor(): BottomSheetDialogFragment() {

    companion object {
        fun show(manager: FragmentManager) {
            InfoBottomSheet().show(manager, "InfoBottomSheet")
        }
    }

    private lateinit var behavior: BottomSheetBehavior<View>

    override fun getTheme(): Int {
        return R.style.AkbBottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_info, container, false)
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        behavior = BottomSheetBehavior.from(view.parent as View)

    }
}
