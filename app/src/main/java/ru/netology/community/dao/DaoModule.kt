package ru.netology.community.dao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.community.dao.event.EventDao
import ru.netology.community.dao.event.EventRemoteKeyDao
import ru.netology.community.dao.post.PostDao
import ru.netology.community.dao.post.PostRemoteKeyDao
import ru.netology.community.db.AppDatabase

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDatabase): PostRemoteKeyDao = db.postRemoteKeyDao()

    @Provides
    fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(db: AppDatabase): EventRemoteKeyDao = db.eventRemoteKeyDao()
}