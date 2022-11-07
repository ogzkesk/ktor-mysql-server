package com.example

import io.ktor.server.application.*
import com.example.plugins.*
import org.ktorm.dsl.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureRouting()
    configureJwt()
}
