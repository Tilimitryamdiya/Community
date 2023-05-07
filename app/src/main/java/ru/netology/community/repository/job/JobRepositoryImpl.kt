package ru.netology.community.repository.job

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.community.api.ApiService
import ru.netology.community.dao.job.JobDao
import ru.netology.community.dto.Job
import ru.netology.community.entity.job.JobEntity
import ru.netology.community.entity.job.toDto
import ru.netology.community.entity.job.toEntity
import ru.netology.community.error.ApiError
import ru.netology.community.error.NetworkError
import ru.netology.community.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService
) : JobRepository {

    override val data = jobDao.getAll()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getJobsById(id: Int) {
        try {
            jobDao.removeAll()
            val response = apiService.getJobsByUserId(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val data = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(data.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(job: Job) {
        try {
            val response = apiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val data = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(JobEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            jobDao.removeById(id)
            val response = apiService.removeJobById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}