package com.gmail.uli153.akihabara3.ui.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.gmail.uli153.akihabara3.databinding.DialogRecordingBinding
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import java.util.*

class RecordingDialog(private val listener: (String?) -> Unit): DialogFragment(), RecognitionListener {

    private var _binding: DialogRecordingBinding? = null
    private val binding: DialogRecordingBinding get() = _binding!!
    private lateinit var speechRecognizer: SpeechRecognizer

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = this.context ?: throw IllegalStateException()

        _binding = DialogRecordingBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context).apply {
            setCancelable(false)
            setView(binding.root)
        }.create()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.setRecognitionListener(this)
        val speechConfig = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechConfig.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechConfig.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        binding.btnClose.setSafeClickListener {
            dialog.dismiss()
        }

        speechRecognizer.startListening(speechConfig)

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val text = binding.labelSpeachText.text.toString()
        listener(text)
        speechRecognizer.setRecognitionListener(null)
        speechRecognizer.stopListening()
        speechRecognizer.destroy()
        _binding = null
    }

    override fun onResults(p0: Bundle?) {
        val text = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.getOrNull(0)
        binding.labelSpeachText.text = text
    }

    override fun onReadyForSpeech(p0: Bundle?) {

    }

    override fun onBeginningOfSpeech() {

    }

    override fun onRmsChanged(p0: Float) {

    }

    override fun onBufferReceived(p0: ByteArray?) {

    }

    override fun onEndOfSpeech() {

    }

    override fun onError(p0: Int) {

    }

    override fun onPartialResults(p0: Bundle?) {

    }

    override fun onEvent(p0: Int, p1: Bundle?) {

    }
}
