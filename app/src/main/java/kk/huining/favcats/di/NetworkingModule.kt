package kk.huining.favcats.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import kk.huining.favcats.BuildConfig
import kk.huining.favcats.api.CatsApi
import kk.huining.favcats.api.RequestInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkingModule {

    @Singleton
    @Provides
    fun provideTestingApi(
        client: OkHttpClient,
        moshi: MoshiConverterFactory,
        coroutine: CoroutineCallAdapterFactory
    ): CatsApi {
        val retrofit = initRetrofit(BASE_URL, client, moshi, coroutine)
        return retrofit.create(CatsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRequestInterceptor(
    ): RequestInterceptor = RequestInterceptor()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        requestInterceptor: RequestInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            //.callTimeout(45, TimeUnit.SECONDS)
            //.readTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor) // Add loggingInterceptor before other interceptors
            .addInterceptor(requestInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return MoshiConverterFactory.create(moshi)
    }

    @Singleton
    @Provides
    fun provideCoroutineCallAdapterFactory(): CoroutineCallAdapterFactory =
        CoroutineCallAdapterFactory()

    private fun initRetrofit(
        url: String,
        client: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
        coroutineCallAdapterFactory: CoroutineCallAdapterFactory
    ): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(url)
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(coroutineCallAdapterFactory)
            .build()


    companion object {
        const val BASE_URL = "https://api.thecatapi.com/v1/"
    }

}
