package ru.netology.community.repository.auth

import ru.netology.community.model.AuthModel
import ru.netology.community.model.MediaModel

interface AuthRepository {
    suspend fun login(login: String, password: String): AuthModel
    suspend fun register(login: String, password: String, name: String): AuthModel
    suspend fun registerWithPhoto(login: String, password: String, name: String, media: MediaModel): AuthModel
}