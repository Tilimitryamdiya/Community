package ru.netology.community.viewmodel

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.community.auth.AppAuth
import ru.netology.community.error.ApiError
import ru.netology.community.model.AuthModel
import ru.netology.community.model.AuthModelState
import ru.netology.community.repository.auth.AuthRepository
import ru.netology.community.dialog.SignOutDialog
import ru.netology.nmedia.dialog.SignInDialog
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    val data: LiveData<AuthModel> = appAuth
        .authState
        .asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = data.value != AuthModel()

    private val _state = MutableLiveData<AuthModelState>()
    val state: LiveData<AuthModelState>
        get() = _state

    fun login(login: String, password: String) = viewModelScope.launch {
        if (login.isNotBlank() && password.isNotBlank()) {
            try {
                _state.value = AuthModelState(loading = true)
                val result = repository.login(login, password)
                appAuth.setAuth(result.id, result.token)
                _state.value = AuthModelState(loggedIn = true)
            } catch (e: Exception) {
                when (e) {
                    is ApiError -> if (e.status == 404) _state.value =
                        AuthModelState(invalidLoginOrPass = true)
                    else -> _state.value = AuthModelState(error = true)
                }
            }
        } else {
            _state.value = AuthModelState(isBlank = true)
        }
        _state.value = AuthModelState()
    }

    fun logout() {
        appAuth.removeAuth()
        _state.value = AuthModelState(notLoggedIn = true)
    }

    fun confirmLogout(manager: FragmentManager) {
        SignOutDialog().show(manager, SignOutDialog.TAG)
    }

    fun isAuthorized(manager: FragmentManager): Boolean {
        return if (appAuth.authState.value != AuthModel()) {
            true
        } else {
            SignInDialog().show(manager, SignInDialog.TAG)
            false
        }
    }
}