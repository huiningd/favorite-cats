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
    private var image: Image? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresentationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        setupFabOnClickListener()
        getImageDetailInfo()
        fetchLargeImageById()
    }

    private fun getImageDetailInfo() {
        image = sharedVM.getCachedImage(imageId)
        updateFabIcon()
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

    private fun setupFabOnClickListener() {
        binding.fab.setOnClickListener { view ->
            image?.let {
                if (isNetworkAvailable()) {
                    val msg: String
                    if (it.isFavorite) {
                        msg = getString(R.string.removing_fav)
                        viewModel.removeFromFavorites(it.id!!, it.favoriteId!!)
                    } else {
                        msg = getString(R.string.adding_fav)
                        viewModel.addToFavorites(it.id!!)
                    }
                    Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
                }
            }
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
            if (uiModel.requestError != null && !uiModel.requestError.consumed) {
                uiModel.requestError.consume()?.let { msg -> handleError(msg) }
            }
            if (uiModel.loadImageSuccess != null && !uiModel.loadImageSuccess.consumed) {
                uiModel.loadImageSuccess.consume()?.let { res -> handleLoadImageSuccess(res) }
            }
            if (uiModel.addToFavoritesSuccess != null && !uiModel.addToFavoritesSuccess.consumed) {
                uiModel.addToFavoritesSuccess.consume()?.let { res -> handleAddToFavoritesSuccess(res) }
            }
            if (uiModel.removeFromFavoritesSuccess != null && !uiModel.removeFromFavoritesSuccess.consumed) {
                uiModel.removeFromFavoritesSuccess.consume()?.let { res -> handleRemoveFromFavoritesSuccess(res) }
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

    private fun handleAddToFavoritesSuccess(favoriteId: String) {
        image?.favoriteId = favoriteId
        sharedVM.toggleImageFavorite(imageId, favoriteId)
        updateFabIcon()
    }

    private fun handleRemoveFromFavoritesSuccess(res: String) {
        image?.favoriteId = null
        sharedVM.toggleImageFavorite(imageId, null)
        updateFabIcon()
    }

    private fun handleError(msg: String) {
        showInfoDialog(msg, "ErrorDialog")
    }

    private fun updateFabIcon() {
        if (image?.isFavorite == true) {
            binding.fab.setImageResource(R.drawable.ic_favorite_24)
        } else {
            binding.fab.setImageResource(R.drawable.ic_favorite_border_24)
        }
    }

}