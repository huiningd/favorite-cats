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


}
