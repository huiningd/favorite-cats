package kk.huining.favcats.ui.common

import androidx.fragment.app.Fragment
import kk.huining.favcats.FavCatsApplication
import kk.huining.favcats.R
import kk.huining.favcats.di.PresentationComponent
import kk.huining.favcats.di.PresentationModule
import kk.huining.favcats.utils.checkIsNetworkAvailable
import timber.log.Timber
import javax.inject.Inject


open class BaseFragment : Fragment() {

    @Inject
    lateinit var dialogsManager: DialogsManager

    protected fun getPresentationComponent(): PresentationComponent {
        return (activity?.application as FavCatsApplication).getApplicationComponent()
            .newPresentationComponent(PresentationModule(requireActivity()))
    }

    protected fun isNetworkAvailable(): Boolean {
        if (!checkIsNetworkAvailable(activity)) {
            showInfoDialog(R.string.network_not_connected, "NetworkNotConnected")
            return false
        }
        return true
    }

    protected fun showInfoDialog(msg: String, tag: String) {
        val dialog = InfoDialogJustDismiss.newInstance(msg)
        dialogsManager.showDialog(dialog, tag)
    }

    protected fun showInfoDialog(msgId: Int, tag: String) {
        showInfoDialog(getString(msgId), tag)
    }

}