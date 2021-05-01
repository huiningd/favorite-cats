package kk.huining.favcats.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kk.huining.favcats.R
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.databinding.FragmentDetailBinding
import kk.huining.favcats.di.viewmodel.ViewModelFactory
import kk.huining.favcats.ui.common.BaseFragment
import javax.inject.Inject

class ImageDetailFragment: BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val args: ImageDetailFragmentArgs by navArgs()

    private lateinit var viewModel: ImageDetailViewModel
    private lateinit var binding: FragmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresentationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory).get(ImageDetailViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        //binding.image = image
        binding.lifecycleOwner = this
        fetchImageById(args.imageId)

        return binding.root
    }

    private fun fetchImageById(imageId: String) {
        if (isNetworkAvailable()) {
            viewModel.fetchImageById(imageId)
            observeUiState()
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner, Observer {
            val uiModel = it ?: return@Observer

            if (uiModel.loadImageError != null && !uiModel.loadImageError.consumed) {
                uiModel.loadImageError.consume()?.let { msg -> handleLoadImageError(msg) }
            }

            if (uiModel.loadImageSuccess != null && !uiModel.loadImageSuccess.consumed) {
                uiModel.loadImageSuccess.consume()?.let { res -> handleLoadImageSuccess(res) }
            }
        })
    }

    private fun handleLoadImageSuccess(image: Image) {
        Glide.with(this)
            .load(image.url)
            .fitCenter()
            .placeholder(R.drawable.ic_pets_24)
            .into(binding.imageView)
    }

    private fun handleLoadImageError(msg: String) {
        showInfoDialog(msg, "ErrorDialog")
    }

}