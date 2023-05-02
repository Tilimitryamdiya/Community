package ru.netology.community.repository.user

import ru.netology.community.dto.User

interface UserRepository {
    suspend fun getUserById(id: Int): User
}