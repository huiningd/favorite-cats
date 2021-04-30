package kk.huining.favcats.data

import kk.huining.favcats.api.CatsApi
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.utils.safeApiCall
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class CatsRemoteDataSource @Inject constructor(
    private val catsApi: CatsApi
) {

    suspend fun getUploadedImages() = safeApiCall(
        call = { requestImages() },
        errorMessage = "Unexpected error when fetching images."
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