package com.gmail.uli153.akihabara3.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.FragmentManager
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.databinding.BottomSheetDeleteProductBinding
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteProductBottomSheet private constructor(private val product: Product): BottomSheetDialogFragment() {

    interface DeleteProductListener {

    }

    companion object {
        fun show(manager: FragmentManager, product: Product) {
            DeleteProductBottomSheet(product).show(manager, "DeleteProductBottomSheet")
        }
    }

    private lateinit var behavior: BottomSheetBehavior<View>

    override fun getTheme(): Int {
        return R.style.AkbBottomSheetDialog
    }

    private lateinit var binding: BottomSheetDeleteProductBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return BottomSheetDeleteProductBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.isDraggable = false
        binding.labelTitle.text = getString(R.string.confirm_delete_product, product.name)
        binding.btnClose.setSafeClickListener {
            dismiss()
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val seekBar = seekBar ?: return

                if (seekBar.progress == seekBar.max) {

                } else {
                    seekBar.progress = 0
                }
            }
        })
    }

}