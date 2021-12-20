package ex.rr.archerysession.file

import android.os.Environment
import ex.rr.archerysession.data.Session
import java.io.File

class SaveFile {

    fun saveToFile(id: Long, content: Session) {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val sessionFile = File(dir, FILE_NAME)
        if(sessionFile.exists()) {
            sessionFile.appendText("\n" +id.toString() + ", " + content.getJSON())
        } else {
            sessionFile.createNewFile()
            sessionFile.writeText(id.toString() + ", " + content.getJSON())
        }
    }

    companion object {
        const val FILE_NAME = "/archerySessions.txt"
    }
}