package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository

class WelcomeViewModel(userRepository: UserRepository) : ViewModel() {

    val userName = userRepository.userName
}
