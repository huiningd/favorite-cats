package kk.huining.favcats.data

import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Favorite
import kk.huining.favcats.data.model.Image
import kotlinx.coroutines.withContext
import timber.log.Timber
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
}