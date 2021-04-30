package kk.huining.favcats.di

import dagger.Component
import kk.huining.favcats.di.viewmodel.ViewModelFactoryModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        NetworkingModule::class,
        ViewModelFactoryModule::class
    ]
)
interface ApplicationComponent {
    fun newPresentationComponent(presentationModule: PresentationModule): PresentationComponent
}


