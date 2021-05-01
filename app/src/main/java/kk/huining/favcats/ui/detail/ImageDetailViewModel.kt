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

    fun fetchImageById(id: String) {
        // only allow one login job at a time
        if (fetchImageByIdJob?.isActive == true) {
            return
        }
        fetchImageByIdJob = launchFetchImageByIdJob(id)
    }

    private fun launchFetchImageByIdJob(id: String): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = repository.fetchImageById(id)
                Timber.d("Login was successful.")
                withContext(Dispatchers.Main) {
                    emitUiState(loadImageSuccess = Event(res))
                }
            } catch (e: IOException) {
                Timber.e(e, "Login failed! error: ${e.message}")
                val err = e.message ?: "login_failed"
                withContext(Dispatchers.Main) {
                    emitUiState(loadImageError = Event(err))
                }
            }
        }
    }

    private fun emitUiState(
        loadImageError: Event<String>? = null,
        loadImageSuccess: Event<Image>? = null,
    ) {
        val uiModel = ImageDetailUiModel(loadImageError, loadImageSuccess)
        _uiState.value = uiModel
    }

}

data class ImageDetailUiModel(
    val loadImageError: Event<String>?,
    val loadImageSuccess: Event<Image>?
)