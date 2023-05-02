package ru.netology.community.repository.event

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.community.api.ApiService
import ru.netology.community.dao.event.EventDao
import ru.netology.community.dao.event.EventRemoteKeyDao
import ru.netology.community.db.AppDatabase
import ru.netology.community.dto.Attachment
import ru.netology.community.dto.Event
import ru.netology.community.dto.FeedItem
import ru.netology.community.dto.Media
import ru.netology.community.entity.event.EventEntity
import ru.netology.community.error.ApiError
import ru.netology.community.error.NetworkError
import ru.netology.community.error.UnknownError
import ru.netology.community.model.MediaModel
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDatabase
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = EventRemoteMediator(
            service = apiService,
            appDb = appDb,
            eventDao = eventDao,
            eventRemoteKeyDao = eventRemoteKeyDao
        ),
        pagingSourceFactory = eventDao::getPagingSource
    ).flow.map { it.map(EventEntity::toDto) }

    override suspend fun save(event: Event) {
        try {
            val response = apiService.createEvent(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun saveWithAttachment(event: Event, media: MediaModel) {
        try {
            val uploadMedia = upload(media)
            val response = apiService.createEvent(
                event.copy(
                    attachment = Attachment(uploadMedia.url, media.type)
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(media: MediaModel): Media {
        val part = MultipartBody.Part.createFormData(
            "file", media.file.name, media.file.asRequestBody()
        )
        val response = apiService.uploadMedia(part)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return requireNotNull(response.body())
    }

    override suspend fun likeById(event: Event) {
        try {
            val response =
                if (!event.likedByMe) {
                    apiService.likeEventById(event.id)
                } else {
                    apiService.dislikeEventById(event.id)
                }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        eventDao.getEventById(id)?.let { eventToDelete ->
            eventDao.removeById(id)
            try {
                val response = apiService.deleteEvent(id)
                if (!response.isSuccessful) {
                    eventDao.insert(eventToDelete)
                    throw ApiError(response.code(), response.message())
                }
            } catch (e: IOException) {
                eventDao.insert(eventToDelete)
                throw NetworkError
            } catch (e: Exception) {
                eventDao.insert(eventToDelete)
                throw UnknownError
            }
        }
    }

    override suspend fun getById(id: Int): Event? =
        eventDao.getEventById(id)?.toDto()

}