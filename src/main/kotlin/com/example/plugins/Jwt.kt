package com.example.plugins

import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import java.net.Authenticator

private val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))


fun Application.configureJwt(){

    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt {
            realm = myRealm
            verifier(tokenManager.verifyJWTToken())

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}