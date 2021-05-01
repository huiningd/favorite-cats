package kk.huining.favcats.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kk.huining.favcats.SharedViewModel
import kk.huining.favcats.ui.detail.ImageDetailViewModel
import kk.huining.favcats.ui.favorite.FavoriteViewModel
import kk.huining.favcats.ui.home.GridViewModel


@Module
abstract class ViewModelFactoryModule {

    /**
     * When dagger asks for an instance of ViewModelProvider.Factory, provide a ViewModelFactory
     */
    @Binds
    internal abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    internal abstract fun bindSharedViewModel(vm: SharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteViewModel::class)
    internal abstract fun bindFavoriteViewModel(vm: FavoriteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GridViewModel::class)
    internal abstract fun bindGridViewModel(vm: GridViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ImageDetailViewModel::class)
    internal abstract fun bindImageDetailViewModel(vm: ImageDetailViewModel): ViewModel

}

