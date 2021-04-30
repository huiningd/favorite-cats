package kk.huining.favcats.data

import timber.log.Timber
import javax.inject.Inject

class CatsRepository @Inject constructor(
    private val remoteDataSource: CatsRemoteDataSource
) {
    suspend fun fetchImages(): Unit {
        when(val result = remoteDataSource.getUploadedImages()) {
            is Result.Success -> {
                Timber.d("Refresh balance from remote was successful")
                Timber.e("result $result")
            }
            is Result.Error -> throw result.exception
        }
    }
}