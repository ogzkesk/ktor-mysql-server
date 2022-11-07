package com.example.routes

import com.example.db.DatabaseConnection
import com.example.entities.NotesEntity
import com.example.entities.UserEntity
import com.example.models.user.UserCredentials
import com.example.models.user.UserResponse
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

private val db = DatabaseConnection.database
private val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

fun Route.authentication() {
    post("/register") {
        val credentials = call.receive<UserCredentials>()

        val username = credentials.username.lowercase()
        val password = credentials.hashedPassword()

        // Checking validation
        if (!credentials.isCredentialsValid()) {
            call.respondText("Please enter valid fields")
            return@post
        }

        // Checking username
        val user = db.from(UserEntity)
            .select()
            .where { UserEntity.username eq username }
            .map {
                it[UserEntity.username]
            }.firstOrNull()

        if (user == null) {
            val response = db.insert(UserEntity) { userEntity ->
                set(userEntity.username, username)
                set(userEntity.password, password)
            }

            if (response == 1) {
                call.respond(HttpStatusCode.OK, UserResponse("User successfully created : $username", success = true))
            } else {
                call.respond(HttpStatusCode.BadRequest, UserResponse("Something went wrong", success = false))
            }

        } else {
            call.respond(HttpStatusCode.BadRequest, UserResponse("User already exists", false))
        }
    }
}

fun Route.login() {
    post("/login") {
        val credentials = call.receive<UserCredentials>()

        val username = credentials.username.lowercase()
        val password = credentials.password

        // Checking validation
        if (!credentials.isCredentialsValid()) {
            call.respond(HttpStatusCode.BadRequest, UserResponse("Fields not valid", success = false))
            return@post
        }


        val response = db.from(UserEntity)
            .select()
            .where { UserEntity.username eq username }
            .map {
                val selectedUserName = it[UserEntity.username]!!
                val selectedPassword = it[UserEntity.password]!! // selectedPassword = hashedPassword.

                UserCredentials(selectedUserName, selectedPassword)
            }.firstOrNull()


        if (response != null && response.username == username) {
            // Check matching hashed pw and normal
            val cryptedPassword = BCrypt.checkpw(password, response.password)

            if (!cryptedPassword) {
                call.respond(HttpStatusCode.BadRequest, UserResponse("Wrong Password", success = false))
                return@post
            }

            // Create JWT TOKEN
            val token = tokenManager.generateJWTToken(response)
            call.respond(HttpStatusCode.OK, UserResponse("User successfully logged in TOKEN : $token", success = true))

        } else {
            call.respond(HttpStatusCode.BadRequest, UserResponse("User doesn't exists", success = false))
        }
    }

    // Need to give Authorization -> Bearer token = user token
    authenticate {
        get("/user"){
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
}