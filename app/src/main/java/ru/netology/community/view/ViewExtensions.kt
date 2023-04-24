package ru.netology.community.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.community.R

fun ImageView.load(url: String) =
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.loading_100dp)
        .error(R.drawable.error_100dp)
        .timeout(10_000)
        .into(this)

fun ImageView.loadAttachment(url: String) = Glide.with(this)
    .load(url)
    .timeout(10_000)
    .into(this)