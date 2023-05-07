package ru.netology.community.repository.job

import kotlinx.coroutines.flow.Flow
import ru.netology.community.dto.Job

interface JobRepository {
    val data: Flow<List<Job>>
    suspend fun getJobsById(id: Int)
    suspend fun save(job: Job)
    suspend fun removeById(id: Int)
}