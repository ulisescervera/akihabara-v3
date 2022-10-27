package com.gmail.uli153.akihabara3.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.extensions.toPx
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.util.*

class SnackBarManager(
    private val context: Context,
    private val parent: ViewGroup,
    private val scope: CoroutineScope
) {

    private val SNACKBAR_UNDO_DURATION = 4000 // ms

    private val snackQueue: Queue<Snackbar> = LinkedList()
    private var currentSnackbar: Snackbar? = null

    fun showRetrySnackbar(message: String, retryListener: () -> Unit, dismissListener: () -> Unit) {
        showSnackbar(message, context.getString(R.string.retry), null, retryListener, dismissListener)
    }

    fun showErrorSnackbar(message: String, listener: () -> Unit) {
        showSnackbar(message, context.getString(R.string.close), null, listener, listener)
    }

    fun showUndoSnackbar(undoMessage: String, listener: () -> Unit) {
        showSnackbar(undoMessage, context.getString(R.string.undo), SNACKBAR_UNDO_DURATION, listener)
    }

    private fun showSnackbar(message: String, buttonTitle: String, duration: Int?, listener: () -> Unit, onDismiss: (() -> Unit)? = null) {
        var snackbar: Snackbar? = Snackbar.make(parent, "", Snackbar.LENGTH_INDEFINITE)

        val view = LayoutInflater.from(context).inflate(R.layout.snack_bar_custom, null)
        val label = view.findViewById<TextView>(R.id.label_message)
        val button = view.findViewById<Button>(R.id.btn_undo)
        val progressBar = view.findViewById<LinearProgressIndicator>(R.id.progress_bar)

        if (duration != null) {
            progressBar.isIndeterminate = false
            progressBar.max = duration
        } else {
            progressBar.isGone = true
        }

        label.text = message
        button.text = buttonTitle
        button.setSafeClickListener {
            snackbar?.dismiss()
            listener()
        }

        val job = if (duration != null) {
            scope.launch(Dispatchers.Main, start = CoroutineStart.LAZY) {
                while (progressBar.progress > 0) {
                    delay(10)
                    progressBar.progress -= 10
                }
                snackbar?.dismiss()
            }
        } else null

        snackbar?.apply {
//            duration = SNACKBAR_MAX_DURATION // hay desfase entre la coroutina y el snackbar
            val rootView = getView() as Snackbar.SnackbarLayout
            val layoutParams = getView().layoutParams as? CoordinatorLayout.LayoutParams
            if (layoutParams != null) {
                getView().layoutParams = layoutParams.apply {
                    gravity = Gravity.BOTTOM
                    val horizontalMargin = 16.toPx.toInt()
                    setMargins(horizontalMargin, 0, horizontalMargin, 100.toPx.toInt())
                }
            }
            rootView.setPadding(0, 0, 0, 0)
            rootView.addView(view, 0)
            addCallback(object: Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    job?.cancel()
                    snackQueue.remove(snackbar)
                    currentSnackbar = null
                    snackQueue.peek()?.show()
                    snackbar = null
                    onDismiss?.invoke()
                }
                override fun onShown(sb: Snackbar?) {
                    super.onShown(sb)
                    currentSnackbar = snackbar
                    if (duration != null) {
                        progressBar.progress = duration
                    }
                    job?.start()
                }
            })
        }

        if (snackQueue.size > 0) {
            if (currentSnackbar == null) {
                snackQueue.clear()
                snackQueue.add(snackbar!!)
                snackbar?.show()
            } else {
                snackQueue.add(snackbar!!)
            }
        } else {
            snackQueue.add(snackbar!!)
            snackbar?.show()
        }
    }
}
