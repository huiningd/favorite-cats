package kk.huining.favcats.di

import dagger.Subcomponent
import kk.huining.favcats.MainActivity
import kk.huining.favcats.ui.detail.ImageDetailFragment
import kk.huining.favcats.ui.favorite.FavoriteFragment
import kk.huining.favcats.ui.home.GridFragment
import kk.huining.favcats.ui.upload.UploadFragment

@Subcomponent(modules = [PresentationModule::class])
interface PresentationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(favoriteFragment: FavoriteFragment)
    fun inject(gridFragment: GridFragment)
    fun inject(imageDetailFragment: ImageDetailFragment)
    fun inject(uploadFragment: UploadFragment)

}