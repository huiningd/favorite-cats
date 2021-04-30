package kk.huining.favcats.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class ApplicationModule(private val application: Application) {

    @Singleton
    @Provides
    fun application(): Application {
        return application
    }

    @Provides
    fun appContext(): Context {
        return application.applicationContext
    }

    /**
     * Returns the SharedPreferences object.
     * For storing a relatively small collection of key-values.
     */
    @Singleton
    @Provides
    fun provideSharedPreferences(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("kk.huining.favcats.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE)
    }

}