package ru.netology.community.dto

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?
)

data class UserPreview(
    val name: String,
    val avatar: String?
)