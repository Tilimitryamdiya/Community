package ru.netology.community.entity.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val id: Int,
) {
    enum class KeyType {
        AFTER, BEFORE
    }
}