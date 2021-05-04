package kk.huining.favcats.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kk.huining.favcats.data.CatsRepository
import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class GridViewModel @Inject constructor(
    private val repository: CatsRepository,
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _uiState = MutableLiveData<GridUiModel>()
    val uiState: LiveData<GridUiModel> = _uiState

    private var getRandomImagesJob: Job? = null
    private var getImagesByBreedJob: Job? = null
    private var getBreedsJob: Job? = null
    private var addToFavoritesJob: Job? = null
    private var removeFromFavoritesJob: Job? = null

    fun getRandomImages() {
        // only allow one login job at a time
        if (getRandomImagesJob?.isActive == true) {
            return
        }
        getRandomImagesJob = launchGetRandomImagesJob()
    }

    fun getImagesByBreed(breed: String) {
        if (getRandomImagesJob?.isActive == true) {
            return
        }
        getImagesByBreedJob = launchGetImagesByBreedJob(breed)
    }

    fun getBreeds() {
        if (getBreedsJob?.isActive == true) {
            return
        }
        getBreedsJob = launchGetBreedsJob()
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

    private fun launchGetRandomImagesJob(): Job {
        _isLoading.value = true
        val job = "get random images"
        // The viewModelScope is bound to ViewModel's lifecycle. When LoginViewModel is destroyed,
        // all the asynchronous work that it is doing will be automatically cancelled.
        return viewModelScope.launch {
            try {
                val res = repository.getRandomImages()
                emitUiState(loadImagesSuccess = Event(res))
                _isLoading.value = false
            } catch (e: IOException) {
                val errMessage = "Failed to $job! error: ${e.message}"
                Timber.e(e, errMessage)
                emitUiState(requestError = Event(errMessage))
                _isLoading.value = false
            }
        }
    }

    fun test() {
        launchGetImagesByBreedJob("amau")
    }
    
    private fun launchGetImagesByBreedJob(breed: String): Job? {
        _isLoading.value = true
        val job = "get images by breed $breed"
        return viewModelScope.launch {
            try {
                val res = repository.getImagesByBreed(breed)
                emitUiState(loadImagesSuccess = Event(res))
                _isLoading.value = false
            } catch (e: IOException) {
                val errMessage = "Failed to $job! error: ${e.message}"
                Timber.e(e, errMessage)
                emitUiState(requestError = Event(errMessage))
                _isLoading.value = false
            }
        }
    }

    private fun launchGetBreedsJob(): Job {
        val job = "get breeds"
        return viewModelScope.launch {
            try {
                val res = repository.getBreeds()
                emitUiState(getBreedsSuccess = Event(res))
            } catch (e: IOException) {
                val errMessage = "Failed to $job! error: ${e.message}"
                Timber.e(e, errMessage)
                emitUiState(getBreedsError = Event(errMessage))
            }
        }
    }

    private fun launchAddToFavoritesJob(imageId: String): Job? {
        val job = "add image with ID $imageId to favorites"
        return viewModelScope.launch {
            try {
                val favoriteId = repository.addToFavorites(imageId)
                emitUiState(addToFavoritesSuccess = Event(Pair(first = imageId, second = favoriteId)))
            } catch (e: IOException) {
                val errMessage = "Failed to $job! error: ${e.message}"
                Timber.e(e, errMessage)
                emitUiState(requestError = Event(errMessage))
            }
        }
    }

    private fun launchRemoveFromFavoritesJob(imageId: String, favId: String): Job? {
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
        loadImagesSuccess: Event<List<Image>>? = null,
        getBreedsSuccess: Event<List<Breed>>? = null,
        addToFavoritesSuccess: Event<Pair<String, String>>? = null,
        removeFromFavoritesSuccess: Event<String>? = null,
        requestError: Event<String>? = null,
        getBreedsError: Event<String>? = null,
    ) {
        val uiModel = GridUiModel(loadImagesSuccess, getBreedsSuccess,
            addToFavoritesSuccess, removeFromFavoritesSuccess, requestError, getBreedsError)
        _uiState.value = uiModel
    }

}

data class GridUiModel(
    val loadImageListSuccess: Event<List<Image>>?,
    val getBreedsSuccess: Event<List<Breed>>?,
    val addToFavoritesSuccess: Event<Pair<String, String>>?,
    val removeFromFavoritesSuccess: Event<String>?,
    val requestError: Event<String>?,
    val getBreedsError: Event<String>?,
)
