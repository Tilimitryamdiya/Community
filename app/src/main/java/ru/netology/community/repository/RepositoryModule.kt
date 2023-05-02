package ru.netology.community.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.community.repository.auth.AuthRepository
import ru.netology.community.repository.auth.AuthRepositoryImpl
import ru.netology.community.repository.event.EventRepository
import ru.netology.community.repository.event.EventRepositoryImpl
import ru.netology.community.repository.post.PostRepository
import ru.netology.community.repository.post.PostRepositoryImpl
import ru.netology.community.repository.user.UserRepository
import ru.netology.community.repository.user.UserRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsPostRepository(postRepository: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindsEventRepository(eventRepository: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    fun bindsAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun bindsUserRepository(userRepository: UserRepositoryImpl): UserRepository
}