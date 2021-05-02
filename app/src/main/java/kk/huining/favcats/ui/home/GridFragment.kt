package kk.huining.favcats.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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


class GridFragment : BaseFragment(), AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var viewModel: GridViewModel
    private lateinit var binding: FragmentGridBinding
    private var imageAdapter: ImageAdapter? = null

    private var clickedFavPos: Int? = null
    private var breeds: List<Breed>? = null

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

        observeUiState()
        setupCatBreedsSelector()
        setupRecyclerView()
        return binding.root
    }

    private fun setupCatBreedsSelector() {
        sharedVM.cachedBreeds.observe( viewLifecycleOwner, Observer { list ->
            if (list.isEmpty()) {
                fetchBreeds()
            } else {
                setupSpinner(list)
                breeds = list
            }
        })
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

    private fun fetchBreeds() {
        if (isNetworkAvailable()) {
            viewModel.getBreeds()
        }
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
            if (uiModel.requestError != null && !uiModel.requestError.consumed) {
                uiModel.requestError.consume()?.let { msg -> handleError(msg) }
            }
            if (uiModel.loadImageListSuccess != null && !uiModel.loadImageListSuccess.consumed) {
                uiModel.loadImageListSuccess.consume()?.let { res -> handleLoadImageSuccess(res) }
            }
            if (uiModel.getBreedsSuccess != null && !uiModel.getBreedsSuccess.consumed) {
                uiModel.getBreedsSuccess.consume()?.let { res -> handleGetBreedsSuccess(res) }
            }
            if (uiModel.getBreedsError != null && !uiModel.getBreedsError.consumed) {
                uiModel.getBreedsError.consume()?.let { _ -> handleGetBreedsError() }
            }
            if (uiModel.addToFavoritesSuccess != null && !uiModel.addToFavoritesSuccess.consumed) {
                uiModel.addToFavoritesSuccess.consume()?.let { res -> handleAddToFavoritesSuccess(res) }
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
        //Timber.d("handleGetBreedsSuccess ${breeds.size}") // 67
        val mutableList = breeds.toMutableList()
        mutableList.add(0, Breed(name = "No breed")) // add 'no breed' as the 1st element
        sharedVM.cacheBreeds(mutableList.toList()) // observer will update UI
    }

    private fun handleLoadImageSuccess(images: List<Image>) {
        sharedVM.cacheSmallImages(images) // observer will update UI
    }

    private fun handleGetBreedsError() {
        binding.breedSelectorTitle.text = getString(R.string.no_breeds)
        binding.breedSpinner.visibility = View.GONE
        showInfoDialog(R.string.failed_to_get_breeds, "ErrorDialog")
    }

    private fun handleError(msg: String) {
        showInfoDialog(msg, "ErrorDialog")
    }

    private fun setupSpinner(breeds: List<Breed>) {
        val breedNames = breeds.map { it.name }
        Timber.e("breedNames $breedNames")
        val spinner = binding.breedSpinner
        spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            breedNames
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter

            //Timber.d("sharedVM.selectedBreedPosition ${sharedVM.selectedBreedPosition}")
            // auto-select previous breed
            sharedVM.selectedBreedPosition.let {
                if (it != 0) spinner.setSelection(it)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val selectedBreedName: String? = parent.getItemAtPosition(pos).toString()
        //Toast.makeText(requireContext(), "$selectedBreedName", Toast.LENGTH_SHORT).show()
        // Only fetch from remote if user selected different breed
        if (pos != sharedVM.selectedBreedPosition && isNetworkAvailable()) {
            val selectedBreed = breeds?.firstOrNull { it.name == selectedBreedName }
            if (selectedBreed?.id != null) {
                viewModel.getImagesByBreed(selectedBreed.id!!)
            } else {
                viewModel.getRandomImages()
            }
        }
        sharedVM.selectedBreedPosition = pos
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    override fun onStop() {
        super.onStop()
        Timber.e("onStop ")
    }
}