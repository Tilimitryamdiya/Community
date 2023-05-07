package ru.netology.community.repository.post

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.community.api.ApiService
import ru.netology.community.dao.post.PostDao
import ru.netology.community.dao.post.PostRemoteKeyDao
import ru.netology.community.dao.wall.WallDao
import ru.netology.community.dao.wall.WallRemoteKeyDao
import ru.netology.community.db.AppDatabase
import ru.netology.community.dto.Attachment
import ru.netology.community.dto.FeedItem
import ru.netology.community.dto.Media
import ru.netology.community.dto.Post
import ru.netology.community.entity.post.PostEntity
import ru.netology.community.entity.wall.WallEntity
import ru.netology.community.error.ApiError
import ru.netology.community.error.NetworkError
import ru.netology.community.error.UnknownError
import ru.netology.community.model.MediaModel
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDatabase,
    private val wallDao: WallDao,
    private val wallRemoteKeyDao: WallRemoteKeyDao
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(
            service = apiService,
            appDb = appDb,
            postDao = postDao,
            postRemoteKeyDao = postRemoteKeyDao
        ),
        pagingSourceFactory = postDao::getPagingSource,
    ).flow
        .map { it.map(PostEntity::toDto) }

    @OptIn(ExperimentalPagingApi::class)
    override fun userWall(id: Int): Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            service = apiService,
            appDb = appDb,
            wallDao = wallDao,
            wallRemoteKeyDao = wallRemoteKeyDao,
            authorId = id
        ),
        pagingSourceFactory = wallDao::getPagingSource,
    ).flow
        .map { it.map(WallEntity::toDto) }

    override suspend fun likeById(post: Post) {
        try {
            val response =
                if (!post.likedByMe) {
                    apiService.likePostById(post.id)
                } else {
                    apiService.dislikePostById(post.id)
                }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.createPost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, media: MediaModel) {
        try {
            val uploadMedia = upload(media)

            val response = apiService.createPost(
                post.copy(
                    attachment = Attachment(uploadMedia.url, media.type),
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(media: MediaModel): Media {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", media.file.name, media.file.asRequestBody()
            )
            val response = apiService.uploadMedia(part)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        postDao.getPostById(id)?.let { postToDelete ->
            postDao.removeById(id)
            try {
                val response = apiService.deletePost(id)
                if (!response.isSuccessful) {
                    postDao.insert(postToDelete)
                    throw ApiError(response.code(), response.message())
                }
            } catch (e: IOException) {
                postDao.insert(postToDelete)
                throw NetworkError
            } catch (e: Exception) {
                postDao.insert(postToDelete)
                throw UnknownError
            }
        }
    }

    override suspend fun getById(id: Int): Post? =
        postDao.getPostById(id)?.toDto()
}