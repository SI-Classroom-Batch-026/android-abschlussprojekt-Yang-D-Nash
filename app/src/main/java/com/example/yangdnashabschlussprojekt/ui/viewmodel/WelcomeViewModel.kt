package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WelcomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userName = MutableStateFlow("...")
    val userName = _userName.asStateFlow()

    init {
        _userName.value = userRepository.getUserName()
    }
}
