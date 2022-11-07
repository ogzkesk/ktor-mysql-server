package com.example.models.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse<T>(
    val data:T,
    val success:Boolean
)
