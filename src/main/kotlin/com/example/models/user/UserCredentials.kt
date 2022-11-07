package com.example.models.user

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UserCredentials(
    val username:String,
    val password:String
){
    fun hashedPassword() : String {
        return BCrypt.hashpw(password,BCrypt.gensalt())
    }

    fun isCredentialsValid() : Boolean {
        return username.length >= 3 && password.length >= 4
    }
}
