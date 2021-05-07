package kk.huining.favcats.data

import kk.huining.favcats.api.CatsApi
import kk.huining.favcats.data.model.*
import kk.huining.favcats.utils.extractServerErrorMessage
import kk.huining.favcats.utils.safeApiCall
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import timber.log.Timber
import java.io.File
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
                Result.Success(list)
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
                Result.Success(list)
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
                if (image != null) Result.Success(image)
                else Result.Error(IOException("Got null when fetching image by ID $id"))
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
                Result.Success(list)
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun getFavorites() = safeApiCall(
        call = { requestGetFavorites() },
        errorMessage = "Unexpected error when fetching my favorite images."
    )

    private suspend fun requestGetFavorites(): Result<List<Favorite>> {
        val response: Response<List<Favorite>> = catsApi.getFavourites()
        return when {
            response.isSuccessful -> {
                val list = response.body() ?: emptyList()
                Result.Success(list)
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun addToFavorites(imageId: String) = safeApiCall(
        call = { requestAddToFavorites(imageId) },
        errorMessage = "Unexpected error when adding image to my favorite."
    )

    private suspend fun requestAddToFavorites(imageId: String): Result<String> {
        val response: Response<AddToFavoriteResponse> =
            catsApi.addToFavourites(AddToFavoriteRequest(image_id = imageId))
        return when {
            response.isSuccessful -> {
                val favoriteId = response.body()?.id
                if (favoriteId != null) Result.Success(favoriteId.toString())
                else Result.Error(IOException("FavoriteId is null!"))
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun getFavoriteById(imageId: String) = safeApiCall(
        call = { requestGetFavoritesById(imageId) },
        errorMessage = "Unexpected error when adding image to my favorite."
    )

    private suspend fun requestGetFavoritesById(imageId: String): Result<Favorite> {
        val response: Response<Favorite> = catsApi.getFavouriteById(imageId)
        return when {
            response.isSuccessful -> {
                val image = response.body()
                if (image != null) Result.Success(image)
                else Result.Error(IOException("Got null when fetching favorite image by id $imageId"))
            }
            else -> {
                val errMessage = "${response.code()} ${response.message()}"
                Timber.e(errMessage)
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun removeFavoriteById(favId: String) = safeApiCall(
        call = { requestRemoveFavoritesById(favId) },
        errorMessage = "Unexpected error when adding image to my favorite."
    )

    private suspend fun requestRemoveFavoritesById(favId: String): Result<String> {
        val response: Response<DefaultResponse> = catsApi.removeFavouriteById(favId)
        return when {
            response.isSuccessful -> {
                Result.Success(response.body()?.message ?: "SUCCESS")
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
                Result.Success(list)
            }
            else -> {
                // Example response {"message":"INVALID_ACCOUNT","status":400,"level":"info"}
                val errMessage = "${response.code()} ${response.raw()}"
                Timber.e(errMessage)
                Timber.e("$response")
                Result.Error(IOException(errMessage))
            }
        }
    }

    suspend fun uploadImageFile(fileToUpload: File, contentType: MediaType) = safeApiCall(
        call = { requestUploadFile(fileToUpload, contentType) },
        errorMessage = "Unexpected error when fetching my uploaded images."
    )

    private suspend fun requestUploadFile(fileToUpload: File, contentType: MediaType): Result<UploadImageResponse> {
        val requestFile: RequestBody = fileToUpload.asRequestBody(contentType)
        val body: MultipartBody.Part = createFormData("file", fileToUpload.name, requestFile)

        val response : Response<UploadImageResponse> = catsApi.uploadImage(body)
        return when {
            response.isSuccessful -> {
                val res = response.body()
                if (res != null) Result.Success(res)
                else Result.Error(IOException("UploadImageResponse is null!"))
            }
            else -> {
                val errMessage = extractServerErrorMessage(response.errorBody()) ?: "Unknown"
                Timber.e(errMessage)
                Timber.e("$response")
                Result.Error(IOException(errMessage))
            }
        }
    }

}