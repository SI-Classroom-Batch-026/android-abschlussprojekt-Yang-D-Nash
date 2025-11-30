package com.example.yangdnashabschlussprojekt.data.model.box

data class AnimatedBox(
    val id: Int,
    val label: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val frameWidth: Int,
    val frameHeight: Int
)