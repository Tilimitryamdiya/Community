package ru.netology.community.repository.post

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.community.dto.FeedItem
import ru.netology.community.dto.Post
import ru.netology.community.model.MediaModel

interface PostRepository  {
    val data: Flow<PagingData<FeedItem>>
    fun userWall(id: Int): Flow<PagingData<FeedItem>>
    suspend fun likeById(post: Post)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, media: MediaModel)
    suspend fun removeById(id: Int)
    suspend fun getById(id: Int): Post?
    suspend fun wallRemoveById(id: Int)
}