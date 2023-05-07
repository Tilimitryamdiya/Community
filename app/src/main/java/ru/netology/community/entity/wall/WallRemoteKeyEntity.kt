package ru.netology.community.entity.wall

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.community.enumeration.RemoteKeyType

@Entity
data class WallRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int,
)