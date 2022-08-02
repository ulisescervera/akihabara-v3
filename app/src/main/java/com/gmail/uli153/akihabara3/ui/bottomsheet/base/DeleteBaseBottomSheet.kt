package com.gmail.uli153.akihabara3.ui.bottomsheet.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.models.Nameable
import com.gmail.uli153.akihabara3.databinding.BottomSheetDeleteBinding
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class DeleteBaseBottomSheet<T: Nameable> constructor(
    private val item: T,
    private val listener: DeleteListener<T>
) : BottomSheetDialogFragment() {

    interface DeleteListener<T> {
        fun onDeleteItem(item: T)
        fun onCancel(item: T) {}
    }

    private lateinit var behavior: BottomSheetBehavior<View>

    override fun getTheme(): Int {
        return R.style.AkbBottomSheetDialog
    }

    private lateinit var binding: BottomSheetDeleteBinding

    protected open val message: String get() {
        return getString(R.string.confirm_delete_product, item.name)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return BottomSheetDeleteBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.isDraggable = false
        binding.labelTitle.text = message
        binding.btnClose.setSafeClickListener {
            dismiss()
            listener.onCancel(item)
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val seekBar = seekBar ?: return

                if (seekBar.progress == seekBar.max) {
                    listener.onDeleteItem(item)
                    dismiss()
                } else {
                    seekBar.progress = 0
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onCancel(item)
    }
}