package kk.huining.favcats.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kk.huining.favcats.R
import kk.huining.favcats.SharedViewModel
import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.databinding.FragmentGridBinding
import kk.huining.favcats.di.viewmodel.ViewModelFactory
import kk.huining.favcats.ui.common.BaseFragment
import kk.huining.favcats.ui.home.GridFragmentDirections.Companion.actionGridToDetail
import timber.log.Timber
import javax.inject.Inject

class GridFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var viewModel: GridViewModel
    private lateinit var binding: FragmentGridBinding
    private var imageAdapter: ImageAdapter? = null
    private var clickedFavPos: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Creates an instance of Presentation component and injects this fragment to the Component.
        // It is advised to call inject(this) before super.onCreate().
        getPresentationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory).get(GridViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_grid, container, false)
        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this
        setupRecyclerView()
        observeUiState()
        return binding.root
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(
            requireContext(),
            ImageOnClickListener { image, view -> onImageClicked(image, view) },
            FavIconOnClickListener { image, view -> onFavoriteClicked(image, view) }
        )
        binding.imageGrid.adapter = imageAdapter
        binding.imageGrid.layoutManager = GridLayoutManager(activity, 3)

        sharedVM.cachedSmallImages.observe( viewLifecycleOwner, Observer { list ->
            if (list.isEmpty()) {
                fetchMore()
            } else {
                Timber.e("##### submitList ${list.size}")
                imageAdapter?.submitList(list as MutableList<Image>?)
            }
        })
    }

    private fun fetchMore() {
        if (isNetworkAvailable()) {
            viewModel.getRandomImages()
        }
    }

    private fun onFavoriteClicked(image: Image, position: Int) {
        clickedFavPos = position
        Timber.e("onFavoriteClicked")
        if (image.id == null) {
            showInfoDialog(R.string.err_fav_no_image_id, "ErrorFavDialog")
            return
        }
        if (isNetworkAvailable()) {
            val msg: String
            if (image.isFavorite) {
                msg = "Removing from favorites"
                viewModel.removeFromFavorites(image.id!!, image.favoriteId!!)
            } else {
                msg = "Adding to favorites"
                viewModel.addToFavorites(image.id!!)
            }
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onImageClicked(image: Image, view: View) {
        Timber.e("onImageClicked ${image.id}")
        if (image.id != null) {
            findNavController().navigate(actionGridToDetail(imageId = image.id!!))
        } else {
            showInfoDialog(R.string.err_detail_no_image_id, "ErrorDetailDialog")
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner, Observer {
            val uiModel = it ?: return@Observer
            if (uiModel.loadImageListError != null && !uiModel.loadImageListError.consumed) {
                uiModel.loadImageListError.consume()?.let { msg -> handleError(msg) }
            }
            if (uiModel.loadImageListSuccess != null && !uiModel.loadImageListSuccess.consumed) {
                uiModel.loadImageListSuccess.consume()?.let { res -> handleLoadImageSuccess(res) }
            }
            if (uiModel.getBreedsError != null && !uiModel.getBreedsError.consumed) {
                uiModel.getBreedsError.consume()?.let { msg -> handleError(msg) }
            }
            if (uiModel.getBreedsSuccess != null && !uiModel.getBreedsSuccess.consumed) {
                uiModel.getBreedsSuccess.consume()?.let { res -> handleGetBreedsSuccess(res) }
            }
            if (uiModel.addToFavoritesError != null && !uiModel.addToFavoritesError.consumed) {
                uiModel.addToFavoritesError.consume()?.let { msg -> handleError(msg) }
            }
            if (uiModel.addToFavoritesSuccess != null && !uiModel.addToFavoritesSuccess.consumed) {
                uiModel.addToFavoritesSuccess.consume()?.let { res -> handleAddToFavoritesSuccess(res) }
            }
            if (uiModel.removeFromFavoritesError != null && !uiModel.removeFromFavoritesError.consumed) {
                uiModel.removeFromFavoritesError.consume()?.let { msg -> handleError(msg) }
            }
            if (uiModel.removeFromFavoritesSuccess != null && !uiModel.removeFromFavoritesSuccess.consumed) {
                uiModel.removeFromFavoritesSuccess.consume()?.let { res -> handleRemoveFromFavoritesSuccess(res) }
            }
        })
    }

    private fun handleAddToFavoritesSuccess(pair: Pair<String, String>) {
        val imageId = pair.first
        val favoriteId = pair.second
        sharedVM.toggleImageFavorite(imageId, favoriteId)
        clickedFavPos?.let { imageAdapter?.notifyItemChanged(it) }
    }

    private fun handleRemoveFromFavoritesSuccess(imageId: String) {
        sharedVM.toggleImageFavorite(imageId, null)
        clickedFavPos?.let { imageAdapter?.notifyItemChanged(it) }
    }

    private fun handleGetBreedsSuccess(breeds: List<Breed>) {
        // TODO
        Timber.e("handleGetBreedsSuccess ${breeds.size}")
    }

    private fun handleLoadImageSuccess(images: List<Image>) {
        sharedVM.cacheSmallImages(images) // observer will update UI
    }

    private fun handleError(msg: String) {
        showInfoDialog(msg, "ErrorDialog")
    }

}