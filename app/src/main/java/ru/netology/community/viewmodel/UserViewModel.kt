package ru.netology.community.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.community.model.FeedModelState
import ru.netology.community.repository.user.UserRepository
import javax.inject.Inject

class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _userDataState = MutableLiveData<FeedModelState>()
    val userDataState: LiveData<FeedModelState>
        get() = _userDataState

    fun getUserById(id: Int) = viewModelScope.launch {
        try {
            userRepository.getUserById(id)
            _userDataState.value = FeedModelState()
        } catch (e: Exception) {
            _userDataState.value = FeedModelState(error = true)
        }
    }

}