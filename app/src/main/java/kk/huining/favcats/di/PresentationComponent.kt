package kk.huining.favcats.di

import dagger.Subcomponent
import kk.huining.favcats.MainActivity
import kk.huining.favcats.ui.favorite.FavoriteFragment

@Subcomponent(modules = [PresentationModule::class])
interface PresentationComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(favoriteFragment: FavoriteFragment)

}