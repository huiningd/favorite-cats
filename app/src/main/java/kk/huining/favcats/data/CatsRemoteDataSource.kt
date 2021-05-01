package kk.huining.favcats.data

import kk.huining.favcats.api.CatsApi
import kk.huining.favcats.data.model.Breed
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.utils.safeApiCall
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class CatsRemoteDataSource @Inject constructor(
    private val catsApi: CatsApi
) {

    suspend fun getRandomImages() = safeApiCall(
        call = { requestGetRandomImages() },
        errorMessage = "Unexpected error when searching images."
    )

    private suspend fun requestGetRandomImages(): Result<List<Image>> {
        val response: Response<List<Image>> = catsApi.getRandomImagesSmall()
        return when {
            response.isSuccessful -> {
                val list = response.body() ?: emptyList()
                return Result.Success(list)
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun getImagesByBreed(breed: String) = safeApiCall(
        call = { requestGetImagesByBreed(breed) },
        errorMessage = "Unexpected error when searching images by breed $breed."
    )

    private suspend fun requestGetImagesByBreed(breed: String): Result<List<Image>> {
        val response: Response<List<Image>> = catsApi.searchImagesByBreed(breed_ids = breed)
        //breed_ids = "abys,aege,abob,acur,asho,awir,amau,amis,bali,bamb"
        return when {
            response.isSuccessful -> {
                val list = response.body() ?: emptyList()
                return Result.Success(list)
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun fetchImageById(id: String) = safeApiCall(
        call = { requestFetchImageById(id) },
        errorMessage = "Unexpected error when fetching image by ID."
    )

    private suspend fun requestFetchImageById(id: String): Result<Image> {
        val response: Response<Image> = catsApi.getImageById(id)
        return when {
            response.isSuccessful -> {
                val image: Image? = response.body()
                return if (image != null) Result.Success(image)
                else Result.Error(IOException("Got null image!"))
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun getBreeds() = safeApiCall(
        call = { requestGetBreeds() },
        errorMessage = "Unexpected error when fetching breeds."
    )

    private suspend fun requestGetBreeds(): Result<List<Breed>> {
        val response: Response<List<Breed>> = catsApi.getBreeds()
        return when {
            response.isSuccessful -> {
                val list = response.body() ?: emptyList()
                return Result.Success(list)
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun getUploadedImages() = safeApiCall(
        call = { requestImages() },
        errorMessage = "Unexpected error when fetching my uploaded images."
    )

    private suspend fun requestImages(): Result<List<Image>> {
        val response: Response<List<Image>> = catsApi.getUploadedImages()
        return when {
            response.isSuccessful -> {
                val list = response.body() ?: emptyList()
                return Result.Success(list)
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

}