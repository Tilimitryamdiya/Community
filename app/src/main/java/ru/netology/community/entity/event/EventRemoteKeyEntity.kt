package ru.netology.community.entity.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.community.enumeration.RemoteKeyType

@Entity
data class EventRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int
)