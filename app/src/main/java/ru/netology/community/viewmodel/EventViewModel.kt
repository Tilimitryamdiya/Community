package ru.netology.community.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.community.dto.Event
import ru.netology.community.dto.FeedItem
import ru.netology.community.enumeration.AttachmentType
import ru.netology.community.enumeration.EventType
import ru.netology.community.model.FeedModelState
import ru.netology.community.model.MediaModel
import ru.netology.community.repository.event.EventRepository
import ru.netology.community.utils.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    published = "",
    coords = null,
    link = null,
    users = emptyMap(),
    datetime = "",
    type = EventType.ONLINE,
    participatedByMe = false
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = cached

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Event>
    get() = _edited

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    fun addPhoto(uri: Uri, file: File, type: AttachmentType) {
        _media.value = MediaModel(uri, file, type)
    }

    fun clearPhoto() {
        _media.value = null
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
                        _eventCreated.value = Unit
                        clearEdited()
                        clearPhoto()
                        _dataState.value = FeedModelState()
                    } catch (e: Exception) {
                        _dataState.value = FeedModelState(error = true)
                    }
                }
            }
        }
    }

    fun edit(event: Event) {
        _edited.value = event
    }
    fun clearEdited() {
        _edited.value = empty
    }
    fun changeContent(
        content: String,
        datetime: String,
        eventType: EventType) {
        val text = content.trim()
        _edited.value = edited.value?.copy(content = text, datetime = datetime, type = eventType)
    }

    fun likeById(event: Event) {
        viewModelScope.launch {
            try {
                repository.likeById(event)
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