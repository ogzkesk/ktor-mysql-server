package com.example.models.note

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val note:String
)
