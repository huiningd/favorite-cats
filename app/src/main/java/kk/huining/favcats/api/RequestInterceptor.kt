package kk.huining.favcats.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestInterceptor @Inject constructor() : Interceptor {

    // curl --location --request GET 'https://api.thecatapi.com/v1/images/search?format=json' \
    //--header 'Content-Type: application/json' \
    //--header 'x-api-key: DEMO-API-KEY'

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        request= request.newBuilder()
            .addHeader(HEADER_API_KEY, API_KEY)
            .build()

        Timber.d("--> Sending request: url ${request.url}, header: ${request.headers}")

        return chain.proceed(request)
    }

    companion object {
        const val HEADER_API_KEY = "x-api-key"
        const val API_KEY = "9b7e282d-2a67-4c7b-a9fd-3f3e4056e949"
    }

}