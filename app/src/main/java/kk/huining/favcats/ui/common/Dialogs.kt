package kk.huining.favcats.ui.common

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kk.huining.favcats.R
import timber.log.Timber

/**
 * A dialog with OK button which onClick just dismisses the dialog.
 */
class InfoDialogJustDismiss : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            //val builder = AlertDialog.Builder(context!!, R.style.AlertDialogCustom)
            val builder = AlertDialog.Builder(requireContext())
            arguments?.let {
                val message = it.getString(INFO_DIALOG_MESSAGE)
                builder.setMessage(message)
                builder.apply {
                    setPositiveButton(R.string.ok) { _, _ ->
                        dismiss()
                    }
                }
                return builder.create()
            }
        } ?: throw IllegalStateException("Context or arguments is null!")
    }

    companion object {
        private const val INFO_DIALOG_MESSAGE = "INFO_DIALOG_MESSAGE"

        fun newInstance(message: String): InfoDialogJustDismiss =
            InfoDialogJustDismiss().apply {
            arguments = Bundle().apply {
                putString(INFO_DIALOG_MESSAGE, message)
            }
        }
    }
}

/**
 * A dialog with OK button which onClick triggers the callback function.
 */
class InfoDialogWithCallback : DialogFragment() {
    // Use this instance of the interface to deliver action events
    internal lateinit var listener: Listener

    interface Listener {
        fun onDialogPositiveClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            val builder = AlertDialog.Builder(requireContext())
            arguments?.let {
                builder.setMessage(it.getString(MESSAGE))
                builder.apply {
                    setPositiveButton(R.string.ok) { _, _ ->
                        listener.onDialogPositiveClick()
                    }
                }
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false) // should not cancel on touch outside!
                return dialog
            }
        } ?: throw IllegalStateException("Context or arguments null")
    }

    companion object {
        private const val MESSAGE = "MESSAGE"

        fun newInstance(message: String): InfoDialogWithCallback =
            InfoDialogWithCallback().apply {
                arguments = Bundle().apply {
                    putString(MESSAGE, message)
                }
            }
    }
}

/**
 * A dialog with CANCEL and OK buttons. CANCEL dismisses the dialog,
 * OK triggers the callback function.
 */
class InfoDialogWithCancelOk : DialogFragment() {
    // Use this instance of the interface to deliver action events
    internal lateinit var listener: Listener

    interface Listener {
        fun onDialogPositiveClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            val builder = AlertDialog.Builder(requireContext())
            arguments?.let {
                builder.setMessage(it.getString(MESSAGE))
                builder.apply {
                    setPositiveButton(R.string.ok) { _, _ ->
                        listener.onDialogPositiveClick()
                    }
                    setNegativeButton(R.string.cancel) { _, _ ->
                        dismiss()
                    }
                }
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false) // should not cancel on touch outside!
                return dialog
            }
        } ?: throw IllegalStateException("Context or arguments null")
    }

    companion object {
        private const val MESSAGE = "MESSAGE"

        fun newInstance(message: String): InfoDialogWithCancelOk =
            InfoDialogWithCancelOk().apply {
                arguments = Bundle().apply {
                    putString(MESSAGE, message)
                }
            }
    }
}

// A dialog with positive button and negative button with callbacks
class DialogWithTwoCallbackButtons() : DialogFragment() {
    // Use this instance of the interface to deliver action events
    internal lateinit var listener: Listener

    interface Listener {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            val builder = AlertDialog.Builder(requireContext())
            arguments?.let {
                builder.setMessage(it.getString(MESSAGE))
                builder.apply {
                    setPositiveButton(it.getInt(POSITIVE_LABEL)) { _, _ ->
                        listener.onPositiveButtonClick()
                    }
                    setNegativeButton(it.getInt(NEGATIVE_LABEL)) { _, _ ->
                        listener.onNegativeButtonClick()
                    }
                }
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false) // should not cancel on touch outside!
                return dialog
            }
        } ?: throw IllegalStateException("Context or arguments null")
    }

    companion object {
        private const val MESSAGE = "MESSAGE"
        private const val POSITIVE_LABEL = "POSITIVE_LABEL"
        private const val NEGATIVE_LABEL = "NEGATIVE_LABEL"

        fun newInstance(posLabel: Int, negLabel: Int, message: String): DialogWithTwoCallbackButtons =
            DialogWithTwoCallbackButtons().apply {
                arguments = Bundle().apply {
                    putString(MESSAGE, message)
                    putInt(POSITIVE_LABEL, posLabel)
                    putInt(NEGATIVE_LABEL, negLabel)
                }
            }
        }
}

//-------------------------------------Dialogs used in specific screen------------------------------

