package com.example.models.note

import kotlinx.serialization.Serializable

@Serializable
data class NotesModel(
    val id:Int,
    val note:String
)
