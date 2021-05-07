package kk.huining.favcats.data

import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Favorite
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.data.model.UploadImageResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class CatsRepository @Inject constructor(
    private val remoteDataSource: CatsRemoteDataSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    suspend fun getRandomImages(): List<Image> {
        return withContext(dispatcherProvider.io) {
            // Blocking network request code
            when(val result = remoteDataSource.getRandomImages()) {
                is Result.Success -> {
                    Timber.d("Fetching images was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun fetchImageById(id: String): Image {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.fetchImageById(id)) {
                is Result.Success -> {
                    Timber.d("Fetching image by ID was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun getImagesByBreed(breed: String): List<Image> {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.getImagesByBreed(breed)) {
                is Result.Success -> {
                    Timber.d("Fetching images by breed $breed was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun getBreeds(): List<Breed> {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.getBreeds()) {
                is Result.Success -> {
                    Timber.d("Fetching breeds was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun addToFavorites(id: String): String {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.addToFavorites(id)) {
                is Result.Success -> {
                    Timber.d("Adding image $id to favorites was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun getFavorites(): List<Favorite> {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.getFavorites()) {
                is Result.Success -> {
                    Timber.d("Fetching favorites was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun getFavoriteImageById(id: String): Favorite {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.getFavoriteById(id)) {
                is Result.Success -> {
                    Timber.d("Fetching favorite image by ID $id was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun removeFavoriteImageById(id: String): String {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.removeFavoriteById(id)) {
                is Result.Success -> {
                    Timber.d("Removing favorite image by fav-ID $id was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun getMyUploadedImages(): List<Image> {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.getUploadedImages()) {
                is Result.Success -> {
                    Timber.d("Fetching my uploads was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    suspend fun uploadFile(fileToUpload: File, contentType: MediaType): UploadImageResponse {
        return withContext(dispatcherProvider.io) {
            when (val result = remoteDataSource.uploadImageFile(fileToUpload, contentType)) {
                is Result.Success -> {
                    Timber.d("Upload image file was successful")
                    result.data
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    // A supervisorScope won’t cancel other children when one of them fails.
    suspend fun fetchRandomImagesAndFavorites(): Pair<List<Image>, List<Favorite>> = supervisorScope {
        // fetch in parallel
        val getRandomImages = async { remoteDataSource.getRandomImages() }
        val getFavorites = async { remoteDataSource.getFavorites() }
        try {
            val imagesRes = getRandomImages.await()
            val favRes = getFavorites.await()
            when {
                imagesRes is Result.Success && favRes is Result.Success -> {
                    val images: List<Image> = imagesRes.data
                    val favs: List<Favorite> = favRes.data
                    return@supervisorScope Pair(first = images, second = favs)
                }
                imagesRes is Result.Error -> throw imagesRes.exception
                favRes is Result.Error -> throw favRes.exception
                else -> throw IOException("Unexpected error in fetchRandomImagesAndFavorites()")
            }
        } catch (e: Throwable) {
            throw IOException(e.message)
        }
    }
}