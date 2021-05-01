package kk.huining.favcats.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kk.huining.favcats.R
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

    private lateinit var viewModel: GridViewModel
    private lateinit var binding: FragmentGridBinding

    private var imageAdapter: ImageAdapter? = null

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
        viewModel.imageList.observe( viewLifecycleOwner, Observer { list ->
            if (list.isEmpty()) {
                fetchMore()
            } else {
                imageAdapter?.submitList(list as MutableList<Image>?)
            }
        })
    }

    private fun fetchMore() {
        if (isNetworkAvailable()) {
            viewModel.getRandomImages()
            observeUiState()
        }
    }

    private fun onFavoriteClicked(image: Image, view: View) {
        Timber.e("onFavoriteClicked")
    }

    private fun onImageClicked(image: Image, view: View) {
        Timber.e("onImageClicked ${image.id}")
        image.id?.let {
            findNavController().navigate( actionGridToDetail(imageId = it))
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner, Observer {
            val uiModel = it ?: return@Observer

            if (uiModel.loadImageListError != null && !uiModel.loadImageListError.consumed) {
                uiModel.loadImageListError.consume()?.let { msg -> handleLoadImageError(msg) }
            }

            if (uiModel.loadImageListSuccess != null && !uiModel.loadImageListSuccess.consumed) {
                uiModel.loadImageListSuccess.consume()?.let { res -> handleLoadImageSuccess(res) }
            }
        })
    }

    private fun handleLoadImageSuccess(imageList: List<Image>) {
        imageAdapter?.submitList(imageList as MutableList<Image>?)
    }

    private fun handleLoadImageError(msg: String) {
        showInfoDialog(msg, "ErrorDialog")
    }

}