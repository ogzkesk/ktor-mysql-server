package com.example.routes

import com.example.db.DatabaseConnection
import com.example.entities.NotesEntity
import com.example.models.note.NoteRequest
import com.example.models.note.NoteResponse
import com.example.models.note.NotesModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*

private val db = DatabaseConnection.database

fun Route.getAllNotes() {
    get("/notes") {

        // Create list of notes.
        val notes = db.from(NotesEntity).select()
            .map {
                val id = it[NotesEntity.id]
                val note = it[NotesEntity.note]

                NotesModel(id ?: -1,note ?: "")
            }

        // Return as a json object.
        call.respond(message = notes, status = HttpStatusCode.OK)

    }
}

fun Route.postItemToDb(){
    post("/postNote"){
        // noteRequest has only note parameter.
        val request = call.receive<NoteRequest>()

        // Insert to db
        val result = db.insert(NotesEntity) {note ->
            set(note.note,request.note)
        }

        // Check status ( only adding 1 item to db, if result = 1 means success )
        if(result == 1 ){
            // Send successfully response to the client
            call.respond(HttpStatusCode.OK, NoteResponse("Value has been successfully inserted",true))
        } else {
            // Send failure response to the client
            call.respond(HttpStatusCode.BadRequest, NoteResponse("Failed to insert value",true))
        }
    }
}

fun Route.getNoteById(){
    get("notes/{id}"){
        val idReq = call.parameters["id"]?.toInt() ?: -1

        val note = db.from(NotesEntity)
            .select()
            .where { NotesEntity.id eq idReq }
            .map {
                val id = it[NotesEntity.id]!!
                val note = it[NotesEntity.note]!!

                NotesModel(id = id, note = note)
            }.firstOrNull()

        if(note != null){
            call.respond(HttpStatusCode.OK, NoteResponse(data = note,true))
        } else {
            call.respond(HttpStatusCode.NotFound,
                NoteResponse("Item not exists",false)
            )
        }
    }
}

fun Route.updateById(){
    // Put for update <------
    put("notes/{id}"){
        val updateId = call.parameters["id"]?.toInt() ?: -1
        val updateItem = call.receive<NoteRequest>()

        val updateNote = db.update(NotesEntity) {notesEntity ->
            set(notesEntity.note,updateItem.note)
            where {
                notesEntity.id eq updateId
            }
        }

        // Success = 1
        if(updateNote == 1 ){
            // success
            call.respond(HttpStatusCode.OK,
                NoteResponse(data = NotesModel(updateId,"Item as ${updateItem.note} updated"), success = true)
            )
        } else {
            // failure
            call.respond(HttpStatusCode.BadRequest, NoteResponse("Item couldn't found", success = false))
        }
    }
}

fun Route.deleteNote(){
    // Delete not post for delete item <------
    delete("notes/{id}"){
        val deleteById = call.parameters["id"]?.toInt() ?: -1

        val result = db.delete(NotesEntity) { notes ->
            notes.id eq deleteById
        }

        if(result == 1 ){
            call.respond(HttpStatusCode.OK, NoteResponse("Item at $deleteById id successfully deleted",true))
        } else {
            call.respond(HttpStatusCode.BadRequest, NoteResponse("Id not exists",false))
        }
    }
}












