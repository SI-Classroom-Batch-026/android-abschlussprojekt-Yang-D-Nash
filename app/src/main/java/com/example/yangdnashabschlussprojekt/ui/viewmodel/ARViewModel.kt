package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ARViewModel : ViewModel() {

    private val _boxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boxes: StateFlow<List<TimedBoundingBox>> = _boxes

}
