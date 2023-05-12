package ru.netology.community.utils

import android.content.Context
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun formatDateTime(date: String): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return parsedDate.format(formatter)
    }

    fun formatDate(date: String): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return parsedDate.format(formatter)
    }

    fun formatTime(date: String): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return parsedDate.format(formatter)
    }

    fun dateToTimestamp(datetime: String) = Instant.parse(datetime).toEpochMilli()

    fun checkLink(link: String): String? {
        if (link.trim().isBlank()) return null
        return if (Patterns.WEB_URL.matcher(link.trim()).matches()) {
            link.trim()
        } else {
            ""
        }
    }
}