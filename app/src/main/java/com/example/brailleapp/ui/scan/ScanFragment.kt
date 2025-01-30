package com.example.brailleapp.ui.scan

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.brailleapp.databinding.FragmentScanBinding
import com.example.brailleapp.retrofit.ApiConfig
import com.example.brailleapp.retrofit.FilePart
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private lateinit var photoFile: File

    private val cropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { inputStream ->
        if (inputStream.resultCode == RESULT_OK && inputStream.data != null) {
            val croppedUri = UCrop.getOutput(inputStream.data!!)
            croppedUri?.let {
                currentImageUri = it
                showImage()
            } ?: run {
                Log.e("UCrop", "Crop failed or canceled, URI is null")
                showToast("Crop gagal")
            }
        } else {
            Log.e("UCrop", "Crop failed or canceled, no data returned")
            showToast("Crop dibatalkan")
        }
    }

    private val cameraLauncher= registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            currentImageUri?.let { uri ->
                val uniqueFileName = "cropped_camera_image_${System.currentTimeMillis()}.jpg"
                val destinationUri = Uri.fromFile(File(requireContext().cacheDir, uniqueFileName))
                UCrop.of(uri, destinationUri)
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(1080, 1080)
                    .getIntent(requireContext())
                    .let { intent ->
                        cropLauncher.launch(intent)
                    }
            }
        } else {
            showToast("Pengambilan gambar dibatalkan")
        }
    }

    // Gallery picker launcher
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            val uniqueFileName = "cropped_image_${System.currentTimeMillis()}.jpg"
            val destinationUri = Uri.fromFile(File(requireContext().cacheDir, uniqueFileName))
            UCrop.of(it, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(1080, 1080)
                .getIntent(requireContext())
                .let { intent ->
                    cropLauncher.launch(intent)
                }
        } ?: Log.d("Gallery", "No photo selected")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set button listeners
        binding.btnCamera.setOnClickListener {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                requestPermissions(arrayOf(REQUIRED_PERMISSION), CAMERA_PERMISSION_CODE)
            }
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnAnalyze.setOnClickListener {
            analyzeImage()
        }

        // Restore image if already selected
        currentImageUri?.let {
            showImage()
        }
    }

    private fun startCamera() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "JPEG_${timeStamp}_"
            photoFile = File.createTempFile(fileName, ".jpg", requireContext().cacheDir).apply {
                currentImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    this
                )
            }
            cameraLauncher.launch(currentImageUri)
        } catch (e: Exception) {
            Log.e("startCamera", "Error starting camera: ${e.message}")
            Toast.makeText(requireContext(), "Gagal memulai kamera", Toast.LENGTH_SHORT).show()
        }
    }


    private fun startGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            // Clear previous image in case of Glide issues
            binding.ivPlaceholder.setImageDrawable(null)
            binding.ivPlaceholder.setImageURI(it)
        } ?: run {
            Log.e("Photo Uri", "URI is null, cannot show image")
            showToast("Gambar tidak ditemukan.")
        }
    }

    private fun analyzeImage() {
        if (currentImageUri != null) {
            val file = File(requireContext().cacheDir, "temp_image.jpg")
            val inputStream = requireContext().contentResolver.openInputStream(currentImageUri!!)
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val imagePart = FilePart(file.absolutePath)

            showLoading(true)

            lifecycleScope.launch(Dispatchers.IO) {
                var isSuccess = false
                var attempts = 0
                val maxRetries = 2

                while (!isSuccess && attempts < maxRetries) {
                    try {
                        val response = ApiConfig.apiService.uploadImage(imagePart)
                        isSuccess = true

                        lifecycleScope.launch(Dispatchers.Main) {
                            showLoading(false)

                            Log.d("UPLOAD_SUCCESS", "Prediction: ${response.prediction}")
                            Log.d("UPLOAD_SUCCESS", "Image URL: ${response.image_url}")

                            PredictionDialogFragment.showDialog(
                                fragmentManager = parentFragmentManager,
                                imageUrl = response.image_url,
                                prediction = response.prediction
                            )
                        }
                    } catch (e: Exception) {
                        attempts++
                        if (attempts >= maxRetries) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                showLoading(false)
                                Log.e("UPLOAD_FAILURE", "Error: ${e.message}")
                                showToast("Gagal mengunggah gambar setelah beberapa percobaan.")
                            }
                        } else {
                            Log.w("UPLOAD_RETRY", "Percobaan upload ulang ($attempts/$maxRetries)...")
                        }
                    }
                }
            }
        } else {
            showToast("Gambar tidak dipilih.")
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val CAMERA_PERMISSION_CODE = 100
    }
}