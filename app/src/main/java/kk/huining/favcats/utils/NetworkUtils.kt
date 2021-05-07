package kk.huining.favcats.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kk.huining.favcats.BuildConfig
import kk.huining.favcats.R
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import kk.huining.favcats.data.Result
import kk.huining.favcats.data.model.ServerErrorResponse


const val TIME_OUT = "time_out"

/**
 * Wrap a suspending API [call] in try/catch. In case an exception is thrown, a [Result.Error] is
 * created based on the [errorMessage].
 */
suspend fun <T : Any> safeApiCall(
    call: suspend () -> Result<T>, errorMessage: String
): Result<T> {
    return try {
        call()
    } catch (e: Throwable) {
        Timber.e(e, "Exception in safeApiCall: %s", e.message)
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
        var msg = if (e.message.isNullOrBlank()) errorMessage else "$errorMessage: ${e.message}"
        if (e is SocketTimeoutException) msg = TIME_OUT
        // An exception was thrown when calling the API so we're converting this to an IOException
        Result.Error(IOException(msg, e))
    }
}

fun checkIsNetworkAvailable(activity: Activity?): Boolean {
    if (activity == null) return false
    val connectivityManager =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = connectivityManager.activeNetwork ?: return false
    val activeNetwork= connectivityManager.getNetworkCapabilities(nw) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        // for other device how are able to connect with Ethernet
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        // for check internet over Bluetooth
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}

fun extractServerErrorMessage(resBody: ResponseBody?): String? {
    // Note: resBody.string() can only be called once, the content is streamed out and becomes empty
    val str = resBody?.string()
    if (!str.isNullOrBlank()) {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<ServerErrorResponse> =
            moshi.adapter(ServerErrorResponse::class.java).lenient()
        return if (str.contains("message")) {
            try {
                val errorResponse = jsonAdapter.fromJson(str)
                errorResponse?.message
            } catch (e: Exception) {
                Timber.e(e, "extractUploadErrorMessage: failed to parse ResponseBody [$str] ")
                null
            }
        } else {
            str // returns the ResponseBody.string directly
        }
    }
    return null
}

fun mapErrorCode(err: String): Int? {
    return when (err) {
        TIME_OUT -> R.string.err_network_call_time_out
        else -> null
    }
}
