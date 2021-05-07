package kk.huining.favcats.ui.upload

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kk.huining.favcats.R
import kk.huining.favcats.SharedViewModel
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.data.model.UploadImageResponse
import kk.huining.favcats.databinding.FragmentUploadBinding
import kk.huining.favcats.di.viewmodel.ViewModelFactory
import kk.huining.favcats.ui.common.BaseFragment
import kk.huining.favcats.ui.upload.CameraFragment.Companion.CAMERA_FRAGMENT_RESULT_KEY
import kk.huining.favcats.ui.upload.CameraFragment.Companion.IMAGE_PATH
import kk.huining.favcats.ui.upload.UploadFragmentDirections.Companion.actionUploadToCamera
import kk.huining.favcats.utils.fileFromContentUri
import kk.huining.favcats.utils.getContentType
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


private const val REQUEST_GET_SINGLE_FILE = 15
private const val REQUEST_STORAGE_PERMISSION = 14

class UploadFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var viewModel: UploadViewModel
    private lateinit var binding: FragmentUploadBinding

    private var imageAdapter: MyUploadedImageAdapter? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresentationComponent().inject(this)
        super.onCreate(savedInstanceState)

        setFragmentResultListener(CAMERA_FRAGMENT_RESULT_KEY) { _, bundle ->
            val imagePath = bundle.getString(IMAGE_PATH)
            Timber.d("imagePath $imagePath")
            val uri: Uri = Uri.parse(imagePath)
            handleImportedImage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory).get(UploadViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.isImageImported = false

        binding.openCameraButton.setOnClickListener { openCamera() }
        binding.openGalleryButton.setOnClickListener { onClickGalleryButton() }
        binding.cancelButton.setOnClickListener { cancelUpload() }
        binding.uploadButton.setOnClickListener { uploadFile() }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume: isImageImported ${binding.isImageImported}")
        if (binding.isImageImported == false) {
            setupRecyclerView()
            observeCachedMyUploads()
        }
        observeUiState()
    }

    private fun fetchMyUploads() {
        if (isNetworkAvailable()) {
            viewModel.getMyUploads()
        }
    }

    private fun setupRecyclerView() {
        imageAdapter = MyUploadedImageAdapter(
            requireContext()
        )
        binding.uploadsList.adapter = imageAdapter
    }

    private fun observeCachedMyUploads() {
        sharedVM.cachedMyUploads.observe( viewLifecycleOwner, Observer { list ->
            if (list.isEmpty()) {
                fetchMyUploads()
            } else {
                Timber.d("myUploads submitList ${list.size}")
                imageAdapter?.submitList(list as MutableList<Image>?)
            }
        })
    }

    private fun openCamera() {
        findNavController().navigate(actionUploadToCamera())
    }

    private fun onClickGalleryButton() {
        if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.d("ask for permission")
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions, REQUEST_STORAGE_PERMISSION)
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_GET_SINGLE_FILE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                openGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode === REQUEST_GET_SINGLE_FILE && resultCode === RESULT_OK) {
                val uri = data!!.data
                //Timber.d("selectedImageUri ${selectedImageUri.toString()}")
                uri?.let {
                    handleImportedImage(it)
                }
            }
        } catch (e: Exception) {
            Timber.e("Gallery file select error $e")
        }
    }

    private fun handleImportedImage(uri: Uri) {
        // Move calculation heavy task to Dispatchers.Default
        lifecycleScope.launch {
            selectedImageUri = uri
            binding.isImageImported = true
            binding.imageView.setImageURI(uri)
        }
    }

    private fun cancelUpload() {
        clearImportedImageView()
        resetRecyclerViewIfNecessary()
    }

    private fun uploadFile() {
        selectedImageUri?.let {
            if (isNetworkAvailable()) {
                val contentType = getContentType(requireContext(), it)
                if (contentType == null) {
                    showInfoDialog("Can't upload file because failed to get content type", "ErrorDialog")
                } else {
                    val file = fileFromContentUri(requireContext(), it)
                    viewModel.uploadFile(file, contentType)
                }
            }
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner, Observer {
            val uiModel = it ?: return@Observer

            if (uiModel.requestError != null && !uiModel.requestError.consumed) {
                uiModel.requestError.consume()?.let { msg -> handleError(msg) }
            }
            if (uiModel.uploadFileSuccess != null && !uiModel.uploadFileSuccess.consumed) {
                uiModel.uploadFileSuccess.consume()?.let { res -> handleUploadSuccess(res) }
            }
            if (uiModel.getMyUploadsSuccess != null && !uiModel.getMyUploadsSuccess.consumed) {
                uiModel.getMyUploadsSuccess.consume()?.let { res -> handleGetMyUploadsSuccess(res) }
            }
        })
    }

    private fun handleError(msg: String) {
        showInfoDialog(msg, "ErrorDialog")
    }

    private fun handleUploadSuccess(res: UploadImageResponse) {
        val state = if (res.approved == 1) "approved" else "pending"
        showInfoDialog("Upload success! Image ID ${res.id}, state: $state", "SuccessDialog")
        clearImportedImageView()
        resetRecyclerViewIfNecessary()
        fetchMyUploads() // fetch my uploads again, because we just added a new image
    }

    private fun handleGetMyUploadsSuccess(res: List<Image>) {
        sharedVM.cacheMyUploads(res)
    }

    private fun clearImportedImageView() {
        binding.imageView.setImageURI(null)
        binding.isImageImported = false
    }

    private fun resetRecyclerViewIfNecessary() {
        // When coming from CameraFragment, the RecyclerView and observer are not setup yet.
        if (binding.uploadsList.adapter == null) {
            setupRecyclerView()
            observeCachedMyUploads()
        }
    }

}