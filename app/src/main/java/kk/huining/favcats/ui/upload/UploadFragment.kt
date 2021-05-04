package kk.huining.favcats.ui.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kk.huining.favcats.R
import kk.huining.favcats.SharedViewModel
import kk.huining.favcats.databinding.FragmentUploadBinding
import kk.huining.favcats.di.viewmodel.ViewModelFactory
import kk.huining.favcats.ui.common.BaseFragment
import kk.huining.favcats.ui.upload.UploadFragmentDirections.Companion.actionUploadToCamera
import javax.inject.Inject


class UploadFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var viewModel: UploadViewModel
    private lateinit var binding: FragmentUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresentationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory).get(UploadViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload, container, false)
        binding.lifecycleOwner = this

        binding.openCameraButton.setOnClickListener { openCamera() }
        binding.openGalleryButton.setOnClickListener { openGallery() }
        return binding.root
    }

    private fun openCamera() {
        findNavController().navigate(actionUploadToCamera())
    }

    private fun openGallery() {
        // TODO
    }
}