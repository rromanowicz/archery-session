package ex.rr.archerysession.file

import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import ex.rr.archerysession.data.DbSession
import ex.rr.archerysession.data.Session
import java.io.File

class FileProcessor {

    fun saveToFile(id: Long, session: Session) {
        try {
            val text = "{\"sessionId\": $id, sessionDetails: ${session.getJSON()}}"
            if (sessionFile.exists()) {
                sessionFile.appendText("\n$text")
            } else {
                sessionFile.createNewFile()
                sessionFile.writeText(text)
            }
            Log.d(this::class.java.name, "SessionId: $id written to file.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeAll(sessionList: MutableList<DbSession>) {
        if (sessionFile.exists()) {
            sessionFile.delete()
        }

        sessionList.forEach {
            saveToFile(
                it.id,
                it.session
            )
        }
    }

    fun getAllFromFile(): MutableMap<Long, Session> {
        val sessionMap: MutableMap<Long, Session> = mutableMapOf()
        if (sessionFile.exists()) {
            sessionFile.forEachLine {
                try {
                    val (id, session) = Gson().fromJson(it, DbSession::class.java)
                    sessionMap[id] = session
                } catch (e: java.lang.Exception) {
                    e.message
                }
            }
        }
        Log.d(this::class.java.name, "Retrieved ${sessionMap.size} records data from file.")
        return sessionMap
    }

    companion object {
        private const val FILE_NAME = "/archerySessions.txt"
        private val DIRECTORY: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val sessionFile = File(DIRECTORY, FILE_NAME)
    }
}