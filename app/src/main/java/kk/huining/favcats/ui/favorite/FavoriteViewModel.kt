package kk.huining.favcats.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kk.huining.favcats.data.CatsRepository
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

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _uiState = MutableLiveData<LoginUiModel>()
    val uiState: LiveData<LoginUiModel> = _uiState

    private var fetchImagesJob: Job? = null

    internal fun fetchImages() {
        // only allow one login job at a time
        if (fetchImagesJob?.isActive == true) {
            return
        }
        fetchImagesJob = launchFetchImagesJob()
    }

    private fun launchFetchImagesJob(): Job {
        // The viewModelScope is bound to ViewModel's lifecycle. When LoginViewModel is destroyed,
        // all the asynchronous work that it is doing will be automatically cancelled.
        return viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Starting ...")
            try {
                repository.fetchImages()
                Timber.d("Login was successful.")
                withContext(Dispatchers.Main) {
                    emitUiState(loginSuccess = Event(200))
                }
            } catch (e: IOException) {
                Timber.e(e, "Login failed! error: ${e.message}")
                val err = e.message ?: "login_failed"
                withContext(Dispatchers.Main) {
                    emitUiState(loginError = Event(err))
                }
            }
        }
    }

    private fun emitUiState(
        loginError: Event<String>? = null,
        loginSuccess: Event<Int>? = null,
        initializeError: Event<String>? = null,
        initializeSuccess: Event<Boolean>? = null
    ) {
        val uiModel =
            LoginUiModel(loginError, loginSuccess, initializeError, initializeSuccess)
        _uiState.value = uiModel
    }

}


/**
 * Update UI in [LoginFragment] according to login result.
 */
data class LoginUiModel(
    val loginError: Event<String>?,
    val loginSuccess: Event<Int>?,
    val initializeError: Event<String>?,
    val initializeSuccess: Event<Boolean>?
)