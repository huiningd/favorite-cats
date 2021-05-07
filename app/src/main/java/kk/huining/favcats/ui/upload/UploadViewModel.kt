package kk.huining.favcats.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kk.huining.favcats.data.CatsRepository
import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.data.model.UploadImageResponse
import kk.huining.favcats.ui.home.GridUiModel
import kk.huining.favcats.utils.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject


class UploadViewModel@Inject constructor(
    private val repository: CatsRepository,
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _uiState = MutableLiveData<UploadUiModel>()
    val uiState: LiveData<UploadUiModel> = _uiState

    private var uploadFileJob: Job? = null

    fun uploadFile(fileToUpload: File, contentType: MediaType) {
        // only allow one login job at a time
        if (uploadFileJob?.isActive == true) {
            return
        }
        uploadFileJob = launchUploadFileJob(fileToUpload, contentType)
    }

    private fun launchUploadFileJob(fileToUpload: File, contentType: MediaType): Job {
        _isLoading.value = true
        val job = "upload image file"
        return viewModelScope.launch {
            try {
                val res = repository.uploadFile(fileToUpload, contentType)
                emitUiState(uploadFileSuccess = Event(res))
                _isLoading.value = false
            } catch (e: IOException) {
                val errMessage = "Failed to $job! error: ${e.message}"
                Timber.e(e, errMessage)
                emitUiState(requestError = Event(errMessage))
                _isLoading.value = false
            }
        }
    }

    private var getMyUploadsJob: Job? = null

    fun getMyUploads() {
        // only allow one login job at a time
        if (getMyUploadsJob?.isActive == true) {
            return
        }
        uploadFileJob = launchGetMyUploadsJob()
    }

    private fun launchGetMyUploadsJob(): Job {
        _isLoading.value = true
        val job = "fetch my uploaded images"
        return viewModelScope.launch {
            try {
                val res = repository.getMyUploadedImages()
                emitUiState(getMyUploadsSuccess = Event(res))
                _isLoading.value = false
            } catch (e: IOException) {
                val errMessage = "Failed to $job! error: ${e.message}"
                Timber.e(e, errMessage)
                emitUiState(requestError = Event(errMessage))
                _isLoading.value = false
            }
        }
    }

    private fun emitUiState(
        getMyUploadsSuccess: Event<List<Image>>? = null,
        uploadFileSuccess: Event<UploadImageResponse>? = null,
        requestError: Event<String>? = null,
    ) {
        val uiModel = UploadUiModel(getMyUploadsSuccess, uploadFileSuccess, requestError)
        _uiState.value = uiModel
    }
}

data class UploadUiModel(
    val getMyUploadsSuccess: Event<List<Image>>?,
    val uploadFileSuccess: Event<UploadImageResponse>?,
    val requestError: Event<String>?,
)