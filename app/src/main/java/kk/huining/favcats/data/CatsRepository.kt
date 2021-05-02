package kk.huining.favcats.data

import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Image
import timber.log.Timber
import javax.inject.Inject

class CatsRepository @Inject constructor(
    private val remoteDataSource: CatsRemoteDataSource
) {
    suspend fun getRandomImages(): List<Image> {
        when(val result = remoteDataSource.getRandomImages()) {
            is Result.Success -> {
                Timber.d("Fetching images was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun fetchImageById(id: String): Image {
        when(val result = remoteDataSource.fetchImageById(id)) {
            is Result.Success -> {
                Timber.d("Fetching image by ID was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getImagesByBreed(breed: String): List<Image> {
        when(val result = remoteDataSource.getImagesByBreed(breed)) {
            is Result.Success -> {
                Timber.d("Fetching images by breed $breed was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getBreeds(): List<Breed> {
        when(val result = remoteDataSource.getBreeds()) {
            is Result.Success -> {
                Timber.d("Fetching breeds was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun addToFavorites(id: String): String {
        when(val result = remoteDataSource.addToFavorites(id)) {
            is Result.Success -> {
                Timber.d("Adding image $id to favorites was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getFavorites(): List<Image> {
        when(val result = remoteDataSource.getFavorites()) {
            is Result.Success -> {
                Timber.d("Fetching favorites was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getFavoriteImageById(id: String): Image {
        when(val result = remoteDataSource.getFavoriteById(id)) {
            is Result.Success -> {
                Timber.d("Fetching favorite image by ID $id was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun removeFavoriteImageById(id: String): String {
        when(val result = remoteDataSource.removeFavoriteById(id)) {
            is Result.Success -> {
                Timber.d("Removing favorite image by fav-ID $id was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getMyUploadedImages(): List<Image> {
        when(val result = remoteDataSource.getUploadedImages()) {
            is Result.Success -> {
                Timber.d("Fetching my uploads was successful")
                Timber.e("result ${result.data}")
                return result.data
            }
            is Result.Error -> throw result.exception
        }
    }
}