package kk.huining.favcats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Favorite
import kk.huining.favcats.data.model.Image
import timber.log.Timber
import javax.inject.Inject


class SharedViewModel @Inject constructor(
    //appContext: Context
): ViewModel() {

    val cachedSmallImages: MutableLiveData<List<Image>> = MutableLiveData(emptyList())
    val cachedBreeds: MutableLiveData<List<Breed>> = MutableLiveData(emptyList())
    val cachedMyUploads: MutableLiveData<List<Image>> = MutableLiveData(emptyList())
    val cachedFavorites: MutableLiveData<List<Favorite>> = MutableLiveData(emptyList())
    var selectedBreedPosition: Int = 0 // default to 0

    fun cacheFavorites(favs: List<Favorite>) {
        cachedFavorites.value = favs
        Timber.e("cacheFavorites ${favs.size}")
    }
    fun cacheImagesWithFav(images: List<Image>) {
        val favs = cachedFavorites.value
        // Mark images favorite by saving favoriteId
        if (favs != null && favs.isNotEmpty()) {
            val hashMap: HashMap<String?, String?> = HashMap()
            favs.forEach { hashMap[it.image_id] = it.id } // key is image id, value is fav id.
            val favImageIds = favs.map { it.image_id }
            images.forEach { image ->
                if (favImageIds.contains(image.id))
                    image.favoriteId = hashMap[image.id]
            }
        }
        cachedSmallImages.value = images
        Timber.d("cacheImages ${images.size}")
    }

    fun clearCachedSmallImages() {
        cachedBreeds.value = emptyList()
    }

    fun cacheBreeds(breeds: List<Breed>) {
        cachedBreeds.value = breeds
        Timber.d("cacheBreeds ${breeds.size}")
    }

    fun cacheMyUploads(images: List<Image>) {
        cachedMyUploads.value = images
        Timber.d("cachedMyUploads ${images.size}")
    }

    fun addToCachedSmallImages(tmp: Image) {
        val cached = cachedSmallImages.value?.toMutableList()
        cached?.add(tmp)
        cached?.let { cacheImagesWithFav(cached) }
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