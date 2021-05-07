package kk.huining.favcats.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kk.huining.favcats.R
import kk.huining.favcats.data.model.Favorite
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.databinding.FragmentFavoriteBinding
import kk.huining.favcats.di.viewmodel.ViewModelFactory
import kk.huining.favcats.ui.common.BaseFragment
import kk.huining.favcats.ui.home.FavIconOnClickListener
import kk.huining.favcats.ui.home.ImageAdapter
import kk.huining.favcats.ui.home.ImageOnClickListener
import timber.log.Timber
import javax.inject.Inject

class FavoriteFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var binding: FragmentFavoriteBinding

    private var imageAdapter: FavoriteImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresentationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory).get(FavoriteViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        // Always fetch favorites from remote because they may have been changed
        fetchFavorites()
    }

    private fun setupRecyclerView() {
        imageAdapter = FavoriteImageAdapter(
            requireContext(),
            FavImageOnClickListener { image -> onImageClicked(image) },
            UnfavoriteOnClickListener { image, pos -> removeFromFavorite(image, pos) }
        )
        binding.favoriteList.adapter = imageAdapter
    }

    private fun fetchFavorites() {
        if (isNetworkAvailable()) {
            viewModel.fetchFavorites()
            observeUiState()
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner, Observer {
            val uiModel = it ?: return@Observer

            if (uiModel.requestError != null && !uiModel.requestError.consumed) {
                uiModel.requestError.consume()?.let { msg -> handleError(msg) }
            }
            if (uiModel.getFavoritesSuccess != null && !uiModel.getFavoritesSuccess.consumed) {
                uiModel.getFavoritesSuccess.consume()?.let { res -> handleGetFavsSuccess(res) }
            }
            if (uiModel.removeFromFavoritesSuccess != null && !uiModel.removeFromFavoritesSuccess.consumed) {
                uiModel.removeFromFavoritesSuccess.consume()?.let { res -> handleRemoveFavSuccess(res) }
            }
        })
    }

    private fun handleGetFavsSuccess(list: List<Favorite>) {
        imageAdapter?.submitList(list as MutableList<Favorite>?)
    }

    private fun handleRemoveFavSuccess(position: Int) {
        imageAdapter?.notifyItemRemoved(position)
        imageAdapter?.updateDataset(position)
    }

    private fun handleError(msg: String) {
        showInfoDialog(msg, "ErrorDialog")
    }

    private fun onImageClicked(fav: Favorite) {
        // do nothing now TODO open in dialogfragment
    }

    private fun removeFromFavorite(favorite: Favorite, position: Int) {
        if (isNetworkAvailable()) {
            favorite.id?.let {
                viewModel.removeFromFavorites(it, position)
                Snackbar.make(binding.root, R.string.removing_fav, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

}