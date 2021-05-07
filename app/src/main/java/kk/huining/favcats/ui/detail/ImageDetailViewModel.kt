package kk.huining.favcats.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kk.huining.favcats.data.CatsRepository
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class ImageDetailViewModel @Inject constructor(
    private val repository: CatsRepository,
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _uiState = MutableLiveData<ImageDetailUiModel>()
    val uiState: LiveData<ImageDetailUiModel> = _uiState

    private var fetchImageByIdJob: Job? = null
    private var addToFavoritesJob: Job? = null
    private var removeFromFavoritesJob: Job? = null

    fun fetchImageById(id: String) {
        // only allow one login job at a time
        if (fetchImageByIdJob?.isActive == true) {
            return
        }
        fetchImageByIdJob = launchFetchImageByIdJob(id)
    }

    fun addToFavorites(imageId: String) {
        if (addToFavoritesJob?.isActive == true) {
            return
        }
        addToFavoritesJob = launchAddToFavoritesJob(imageId)
    }

    fun removeFromFavorites(imageId: String, favId: String) {
        if (removeFromFavoritesJob?.isActive == true) {
            return
        }
        removeFromFavoritesJob = launchRemoveFromFavoritesJob(imageId, favId)
    }

    private fun launchFetchImageByIdJob(id: String): Job {
        val job = "fetch image $id"
        _isLoading.value = true
        return viewModelScope.launch {
            try {
                val res = repository.fetchImageById(id)
                emitUiState(loadImageSuccess = Event(res))
                _isLoading.value = false
            } catch (e: IOException) {
                val err = "Failed to $job. error: ${e.message}"
                Timber.e(e, err)
                emitUiState(requestError = Event(err))
                _isLoading.value = false
            }
        }
    }

    private fun launchAddToFavoritesJob(imageId: String): Job {
        val job = "add image with ID $imageId to favorites"
        return viewModelScope.launch {
            try {
                val favoriteId = repository.addToFavorites(imageId)
                emitUiState(addToFavoritesSuccess = Event(favoriteId))
            } catch (e: IOException) {
                val errMessage = "Failed to $job! error: ${e.message}"
                Timber.e(e, errMessage)
                emitUiState(requestError = Event(errMessage))
            }
        }
    }

    private fun launchRemoveFromFavoritesJob(imageId: String, favId: String): Job {
        val job = "remove image with fav-ID $favId from favorites"
        return viewModelScope.launch {
            try {
                val res = repository.removeFavoriteImageById(favId)
                if (res == "SUCCESS") {
                    emitUiState(removeFromFavoritesSuccess = Event(imageId))
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
        loadImageSuccess: Event<Image>? = null,
        addToFavoritesSuccess: Event<String>? = null,
        removeFromFavoritesSuccess: Event<String>? = null
    ) {
        val uiModel = ImageDetailUiModel(requestError, loadImageSuccess,
            addToFavoritesSuccess, removeFromFavoritesSuccess)
        _uiState.value = uiModel
    }

}

data class ImageDetailUiModel(
    val requestError: Event<String>?,
    val loadImageSuccess: Event<Image>?,
    val addToFavoritesSuccess: Event<String>?,
    val removeFromFavoritesSuccess: Event<String>?,
)