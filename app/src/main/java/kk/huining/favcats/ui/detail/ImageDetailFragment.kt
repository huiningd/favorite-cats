package kk.huining.favcats.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kk.huining.favcats.R
import kk.huining.favcats.SharedViewModel
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.databinding.FragmentDetailBinding
import kk.huining.favcats.di.viewmodel.ViewModelFactory
import kk.huining.favcats.ui.common.BaseFragment
import javax.inject.Inject

class ImageDetailFragment: BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val sharedVM: SharedViewModel by activityViewModels()
    private val args: ImageDetailFragmentArgs by navArgs()

    private lateinit var viewModel: ImageDetailViewModel
    private lateinit var binding: FragmentDetailBinding
    private lateinit var imageId: String

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        imageId = args.imageId
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        binding.imageID = getString(R.string.image_id_s, imageId)
        setupFab()
        getImageDetailInfo()
        fetchLargeImageById()
    }

    private fun getImageDetailInfo() {
        val image = sharedVM.getCachedImage(imageId)
        val breeds = image?.breeds
        if (breeds != null && breeds.isNotEmpty()) {
            val breed = breeds[0] // TODO loop list, to string
            binding.breedName = getString(R.string.breed_name_s, breed.name)
            binding.breedOrigin = getString(R.string.breed_origin_s, breed.origin)
            binding.breedDescription = getString(R.string.breed_des_s, breed.description)
        } else {
            binding.breedName = getString(R.string.breed_name_unknown)
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun fetchLargeImageById() {
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