package kk.huining.favcats

import android.app.Application
import kk.huining.favcats.di.ApplicationComponent
import kk.huining.favcats.di.ApplicationModule
import kk.huining.favcats.di.DaggerApplicationComponent
import timber.log.Timber
import timber.log.Timber.DebugTree

class FavCatsApplication : Application() {

    private lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        // Note: If Android Studio complaints DaggerApplicationComponent is not found,
        // that's because it is not created yet. Just build the project, Dagger will create it.
        appComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    fun getApplicationComponent(): ApplicationComponent {
        return appComponent
    }

}

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        // TODO: add Firebase to the project and send error reports to Crashlytics.
    }
}
