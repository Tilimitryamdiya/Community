package ru.netology.community.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.time.format.DateTimeFormatter
import java.util.*

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun formatDateTime(value: String): String {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).parse(value)
        val formatter = DateTimeFormatter.ISO_INSTANT
        return formatter.format(date?.toInstant())
    }
}