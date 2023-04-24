package ru.netology.community.viewmodel

import android.net.Uri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.community.auth.AppAuth
import ru.netology.community.dto.FeedItem
import ru.netology.community.dto.Post
import ru.netology.community.model.AuthModel
import ru.netology.community.model.FeedModelState
import ru.netology.community.model.MediaModel
import ru.netology.community.repository.post.PostRepository
import ru.netology.nmedia.dialog.SignInDialog
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    coords = null,
    link = null,
    content = "",
    published = "",
    mentionedMe = false,
    users = emptyMap()
)

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {
    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = cached

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media


    fun changePhoto(uri: Uri, file: File) {
        _media.value = MediaModel(uri, file)
    }

    fun clearPhoto() {
        _media.value = null
    }

    fun isAuthorized(manager: FragmentManager): Boolean {
        return if (appAuth.authState.value != AuthModel()) {
            true
        } else {
            SignInDialog().show(manager, SignInDialog.TAG)
            false
        }
    }

    fun save() {
        edited.value?.let {
            if (it !== empty) {
                viewModelScope.launch {
                    try {
                        when (val media = media.value) {
                            null -> repository.save(it)
                            else -> {
                                repository.saveWithAttachment(it, media)
                            }
                        }
                        edited.value = empty
                        clearPhoto()
                        _dataState.value = FeedModelState()
                    } catch (e: Exception) {
                        _dataState.value = FeedModelState(error = true)
                    }
                }
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
        viewModelScope.launch {
            try {
                repository.likeById(post)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }

        }
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}