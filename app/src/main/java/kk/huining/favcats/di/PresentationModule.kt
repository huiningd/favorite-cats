package kk.huining.favcats.di

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import kk.huining.favcats.ui.common.DialogsManager


@Module
class PresentationModule(private val activity: FragmentActivity) {

    @Provides
    fun provideActivity(): Activity = activity

    @Provides
    fun getDialogsManager(fragmentManager: FragmentManager): DialogsManager {
        return DialogsManager(fragmentManager)
    }

    @Provides
    fun fragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }

}