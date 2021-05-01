package kk.huining.favcats.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        // Check "No-Authentication" to see if a request needs authorization header.
        request = if (request.header("No-Authentication") == null) {
            Timber.d("Should add API key to header")
            request.newBuilder()
                .addHeader(HEADER_API_KEY, API_KEY)
                .build()
        } else {
            request.newBuilder()
                .build()
        }
        //Timber.d("--> Sending request: url ${request.url}, header: ${request.headers}")
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_API_KEY = "x-api-key"
        const val API_KEY = "9b7e282d-2a67-4c7b-a9fd-3f3e4056e949"
    }

}