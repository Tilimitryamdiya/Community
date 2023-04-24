package ru.netology.community.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.netology.community.R

class SignOutDialog : DialogFragment() {

    interface ConfirmationListener {
        fun confirmButtonClicked()
    }

    private lateinit var listener: ConfirmationListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        listener = activity as ConfirmationListener

        return AlertDialog.Builder(requireContext())
            .setMessage(R.string.confirm_signOut)
            .setPositiveButton(R.string.sign_out) { _, _ ->
                listener.confirmButtonClicked()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }


    companion object {
        const val TAG = "SignOutDialog"
    }
}