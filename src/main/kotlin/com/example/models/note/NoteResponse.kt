package com.example.models.note

import kotlinx.serialization.Serializable

@Serializable
data class NoteResponse<T>(
    val data: T,
    val success: Boolean
)
