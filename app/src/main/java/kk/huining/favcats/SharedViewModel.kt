package kk.huining.favcats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Image
import timber.log.Timber
import javax.inject.Inject


class SharedViewModel @Inject constructor(
    //appContext: Context
): ViewModel() {

    val cachedSmallImages: MutableLiveData<List<Image>> = MutableLiveData(emptyList())
    val cachedBreeds: MutableLiveData<List<Breed>> = MutableLiveData(emptyList())
    var selectedBreedPosition: Int = 0 // default to 0

    fun cacheSmallImages(images: List<Image>) {
        cachedSmallImages.value = images
        Timber.e("cacheImages ${images.size}")
    }

    fun clearCachedSmallImages() {
        cachedBreeds.value = emptyList()
    }

    fun cacheBreeds(breeds: List<Breed>) {
        cachedBreeds.value = breeds
        Timber.e("##### cacheBreeds ${breeds.size}")
    }

    fun addToCachedSmallImages(tmp: Image) {
        val cached = cachedSmallImages.value?.toMutableList()
        cached?.add(tmp)
        cached?.let { cacheSmallImages(cached) }
    }

    fun toggleImageFavorite(imageId: String, favoriteId: String? = null) {
        cachedSmallImages.value?.let { list ->
            val image = list.firstOrNull { it.id == imageId }
            image?.favoriteId = favoriteId
            cachedSmallImages.value = list // postValue to notify observers

            if (favoriteId != null) Timber.d("Image $imageId is marked as favorite")
            else Timber.d("Image $imageId is marked as not-favorite")
        }
    }

    fun getCachedImage(imageId: String): Image? {
        cachedSmallImages.value?.let { list ->
            return list.firstOrNull { it.id == imageId }
        }
        return null
    }
}