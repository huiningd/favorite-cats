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

    private val _imageList = MutableLiveData<List<Image>>().apply {
        value = emptyList()
    }
    val imageList: LiveData<List<Image>> = _imageList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _uiState = MutableLiveData<GridUiModel>()
    val uiState: LiveData<GridUiModel> = _uiState

    private var getRandomImagesJob: Job? = null
    private var getImagesByBreedJob: Job? = null
    private var getBreedsJob: Job? = null

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

    private fun launchGetRandomImagesJob(): Job {
        val job = "get random images"
        // The viewModelScope is bound to ViewModel's lifecycle. When LoginViewModel is destroyed,
        // all the asynchronous work that it is doing will be automatically cancelled.
        return viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Starting ...")
            try {
                val res = repository.getRandomImages()
                withContext(Dispatchers.Main) {
                    emitUiState(loadImagesSuccess = Event(res))
                }
            } catch (e: IOException) {
                Timber.e(e, "Failed to $job! error: ${e.message}")
                val err = e.message ?: "Failed to $job"
                withContext(Dispatchers.Main) {
                    emitUiState(loadImagesError = Event(err))
                }
            }
        }
    }
    
    private fun launchGetImagesByBreedJob(breed: String): Job? {
        val job = "get images by breed $breed"
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = repository.getImagesByBreed(breed)
                withContext(Dispatchers.Main) {
                    emitUiState(loadImagesSuccess = Event(res))
                }
            } catch (e: IOException) {
                Timber.e(e, "Failed to $job! error: ${e.message}")
                val err = e.message ?: "Failed to $job"
                withContext(Dispatchers.Main) {
                    emitUiState(loadImagesError = Event(err))
                }
            }
        }
    }

    private fun launchGetBreedsJob(): Job {
        val job = "get breeds"
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = repository.getBreeds()
                withContext(Dispatchers.Main) {
                    emitUiState(getBreedsSuccess = Event(res))
                }
            } catch (e: IOException) {
                Timber.e(e, "Failed to $job! error: ${e.message}")
                val err = e.message ?: "Failed to $job"
                withContext(Dispatchers.Main) {
                    emitUiState(getBreedsError = Event(err))
                }
            }
        }
    }

    private fun emitUiState(
        loadImagesError: Event<String>? = null,
        getBreedsError: Event<String>? = null,
        loadImagesSuccess: Event<List<Image>>? = null,
        getBreedsSuccess: Event<List<Breed>>? = null,
    ) {
        val uiModel = GridUiModel(loadImagesError, getBreedsError, loadImagesSuccess, getBreedsSuccess)
        _uiState.value = uiModel
    }

}


data class GridUiModel(
    val loadImageListError: Event<String>?,
    val getBreedsError: Event<String>?,
    val loadImageListSuccess: Event<List<Image>>?,
    val getBreedsSuccess: Event<List<Breed>>?
)