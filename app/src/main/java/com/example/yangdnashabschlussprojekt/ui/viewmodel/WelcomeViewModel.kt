package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.AppUser
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WelcomeViewModel(userRepository: UserRepository) : ViewModel() {

    val currentUser: StateFlow<AppUser?> = userRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val userName: StateFlow<String> = userRepository.currentUser
        .map { it?.name ?: "Gast" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Gast")
}
