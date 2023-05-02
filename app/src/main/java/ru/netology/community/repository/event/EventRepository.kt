package ru.netology.community.repository.event

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.community.dto.Event
import ru.netology.community.dto.FeedItem
import ru.netology.community.model.MediaModel

interface EventRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun save(event: Event)
    suspend fun saveWithAttachment(event: Event, media: MediaModel)
    suspend fun likeById(event: Event)
    suspend fun removeById(id: Int)
    suspend fun getById(id: Int): Event?
}