package kk.huining.favcats.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kk.huining.favcats.data.CatsRepository
import javax.inject.Inject

class UploadViewModel@Inject constructor(
    private val repository: CatsRepository,
): ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}