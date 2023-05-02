package ru.netology.community.entity.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.community.enumeration.RemoteKeyType

@Entity
data class PostRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int,
)