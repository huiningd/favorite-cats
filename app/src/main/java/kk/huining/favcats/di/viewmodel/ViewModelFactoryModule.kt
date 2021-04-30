package kk.huining.favcats.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kk.huining.favcats.ui.favorite.FavoriteViewModel


@Module
abstract class ViewModelFactoryModule {

    /**
     * When dagger asks for an instance of ViewModelProvider.Factory, provide a ViewModelFactory
     */
    @Binds
    internal abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
/*
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun bindMainActivityViewModel(vm: MainActivityViewModel): ViewModel
*/
    @Binds
    @IntoMap
    @ViewModelKey(FavoriteViewModel::class)
    internal abstract fun bindFavoriteViewModel(vm: FavoriteViewModel): ViewModel

}

