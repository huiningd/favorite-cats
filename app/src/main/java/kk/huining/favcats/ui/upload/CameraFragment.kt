package kk.huining.favcats.ui.upload

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kk.huining.favcats.MainActivity
import kk.huining.favcats.R
import kk.huining.favcats.ui.upload.CameraFragmentDirections.Companion.actionCameraToPermission
import kk.huining.favcats.ui.upload.CameraFragmentDirections.Companion.actionCameraToUpload
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment: Fragment() {

    private lateinit var container: ConstraintLayout
    private lateinit var viewFinder: PreviewView
    private lateinit var outputDirectory: File

    // Blocking camera operations are performed using this executor
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var takePhotoButton: Button

    private var imageCapture: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_camera, container, false)

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ConstraintLayout
        viewFinder = container.findViewById(R.id.view_finder)
        takePhotoButton = container.findViewById(R.id.camera_capture_button)

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Determine the output directory
        outputDirectory = MainActivity.getOutputDirectory(requireContext())

        // Wait for the views to be properly laid out
        viewFinder.post {
            startCamera()
        }
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!CameraPermissionFragment.hasPermissions(requireContext())) {
            //findNavController().navigate(actionCameraToPermission())
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(actionCameraToPermission())
        }
    }

    private fun startCamera() {
        // Create an instance of the ProcessCameraProvider. This is used to bind the lifecycle of
        // cameras to the lifecycle owner. This eliminates the task of opening and closing the
        // camera since CameraX is lifecycle-aware.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // In the Runnable, add a ProcessCameraProvider. This is used to bind the lifecycle of
            // the camera to the LifecycleOwner within the application's process.
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }
            // Init ImageCapture
            imageCapture = ImageCapture.Builder().build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                // Enable takePhotoButton
                takePhotoButton.setOnClickListener { takePhoto() }
            } catch(exc: Exception) {
                Timber.e("Use case binding failed $exc")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        Timber.e("takePhoto")
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Timber.e("Photo capture failed: $exc")
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                    Timber.e(msg)
                    popBackToUploadScreen(savedUri)
                }
            })
    }

    private fun popBackToUploadScreen(savedUri: Uri) {
        setFragmentResult(CAMERA_FRAGMENT_RESULT_KEY,
            bundleOf(IMAGE_PATH to savedUri.toString()))
        findNavController().navigate(actionCameraToUpload())
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val CAMERA_FRAGMENT_RESULT_KEY = "CAMERA_FRAGMENT_RESULT_KEY"
        const val IMAGE_PATH = "capturedImageLocation"
    }

}