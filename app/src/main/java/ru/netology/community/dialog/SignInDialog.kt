package ru.netology.nmedia.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.netology.community.R

class SignInDialog : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        activity.let {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.sign_in)
                .setPositiveButton(R.string.sign_in) { _, _ ->
                    findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
        }

    companion object {
        const val TAG = "SignInDialog"
    }
}