package ru.netology.community.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.community.dao.post.PostDao
import ru.netology.community.dao.post.PostRemoteKeyDao
import ru.netology.community.entity.CoordinatesConverter
import ru.netology.community.entity.ListIntConverter
import ru.netology.community.entity.MapUsersPrevConverter
import ru.netology.community.entity.post.PostEntity
import ru.netology.community.entity.post.PostRemoteKeyEntity

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListIntConverter::class, MapUsersPrevConverter::class, CoordinatesConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}