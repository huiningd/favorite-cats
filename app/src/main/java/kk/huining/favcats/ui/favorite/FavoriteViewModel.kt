package kk.huining.favcats.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kk.huining.favcats.data.CatsRepository
import kk.huining.favcats.data.model.Favorite
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.ui.detail.ImageDetailUiModel
import kk.huining.favcats.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(
    private val repository: CatsRepository,
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _uiState = MutableLiveData<FavoriteUiModel>()
    val uiState: LiveData<FavoriteUiModel> = _uiState

    private var fetchFavoritesJob: Job? = null

    fun fetchFavorites() {
        // only allow one login job at a time
        if (fetchFavoritesJob?.isActive == true) {
            return
        }
        fetchFavoritesJob = launchFetchFavoritesJob()
    }

    private fun launchFetchFavoritesJob(): Job {
        _isLoading.value = true
        val job = "fetch favorites"
        return viewModelScope.launch {
            try {
                val res = repository.getFavorites()
                emitUiState(getFavoritesSuccess = Event(res))
                _isLoading.value = false
            } catch (e: IOException) {
                val errMsg = "Failed to $job: error: ${e.message}"
                Timber.e(e, errMsg)
                emitUiState(requestError = Event(errMsg))
                _isLoading.value = false
            }
        }
    }

    private var removeFromFavoritesJob: Job? = null

    fun removeFromFavorites(favId: String, pos: Int) {
        if (removeFromFavoritesJob?.isActive == true) {
            return
        }
        removeFromFavoritesJob = launchRemoveFromFavoritesJob(favId, pos)
    }

    private fun launchRemoveFromFavoritesJob(favId: String, pos: Int): Job? {
        val job = "remove image with fav-ID $favId from favorites"
        return viewModelScope.launch {
            try {
                val res = repository.removeFavoriteImageById(favId)
                if (res == "SUCCESS") {
                    emitUiState(removeFromFavoritesSuccess = Event(pos))
                }
            } catch (e: IOException) {
                val errMessage = "Failed to $job! error: ${e.message}"
                Timber.e(e, errMessage)
                emitUiState(requestError = Event(errMessage))
            }
        }
    }

    private fun emitUiState(
        requestError: Event<String>? = null,
        removeFromFavoritesSuccess: Event<Int>? = null,
        getFavoritesSuccess: Event<List<Favorite>>? = null,
    ) {
        val uiModel = FavoriteUiModel(requestError, removeFromFavoritesSuccess, getFavoritesSuccess)
        _uiState.value = uiModel
    }

}

data class FavoriteUiModel(
    val requestError: Event<String>?,
    val removeFromFavoritesSuccess: Event<Int>?,
    val getFavoritesSuccess: Event<List<Favorite>>?
)