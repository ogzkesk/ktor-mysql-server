package com.example.plugins

import com.example.routes.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {
    

    routing {
        getAllNotes()
        postItemToDb()
        getNoteById()
        updateById()
        deleteNote()
        authentication()
        login()

        static("/static") {
            resources("static")
        }
    }
}
