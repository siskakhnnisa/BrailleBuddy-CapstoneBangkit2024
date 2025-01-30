package com.example.brailleapp.ui.scan

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.brailleapp.R
import com.google.android.material.button.MaterialButton

class PredictionDialogFragment : DialogFragment() {

    private var imageUrl: String? = null
    private var prediction: String? = null

    companion object {
        private const val TAG = "PredictionDialog"

        const val ARG_IMAGE_URL = "image_url"
        const val ARG_PREDICTION = "prediction"

        fun newInstance(imageUrl: String, prediction: String): PredictionDialogFragment {
            val fragment = PredictionDialogFragment()
            val args = Bundle().apply {
                putString(ARG_IMAGE_URL, imageUrl)
                putString(ARG_PREDICTION, prediction)
            }
            fragment.arguments = args
            return fragment
        }

        fun showDialog(
            fragmentManager: FragmentManager,
            imageUrl: String,
            prediction: String
        ) {
            // Hapus dialog sebelumnya jika ada
            val existingDialog = fragmentManager.findFragmentByTag(TAG) as? PredictionDialogFragment
            existingDialog?.dismiss()

            // Tampilkan dialog baru
            val dialog = newInstance(imageUrl, prediction)
            dialog.show(fragmentManager, TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(ARG_IMAGE_URL)
            prediction = it.getString(ARG_PREDICTION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_prediction_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivUploadedImage = view.findViewById<ImageView>(R.id.ivUploadedImage)
        val tvPredictionResult = view.findViewById<TextView>(R.id.tvPredictionResult)
        val btnClose = view.findViewById<MaterialButton>(R.id.btnClose)

        // Set image and prediction result
        Glide.with(this)
            .load(imageUrl)
            .error(R.drawable.ic_error_image)
            .into(ivUploadedImage)
        Log.d(TAG, "Loaded image URL: $imageUrl")

        tvPredictionResult.text = prediction

        // Close the dialog
        btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(true)
        }
    }
}
