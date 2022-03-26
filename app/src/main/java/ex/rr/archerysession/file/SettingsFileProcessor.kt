package ex.rr.archerysession.file

import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import ex.rr.archerysession.data.Settings
import java.io.File

class SettingsFileProcessor {

    private fun saveToFile(settings: Settings) {
        try {
            val text = settings.getJSON()
            if (settingsFile.exists()) {
                settingsFile.appendText("\n$text")
            } else {
                settingsFile.createNewFile()
                settingsFile.writeText(text)
            }
            Log.d(this::class.java.name, "Settings written to file.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeSettings(settings: Settings) {
        if (settingsFile.exists()) {
            settingsFile.delete()
        }
        Log.e("SETTINGS_DB", settings.getJSON())
        saveToFile(settings)
    }

    fun readSettings(): Settings {
        var settings: Settings? = null
        if (settingsFile.exists()) {
            settingsFile.forEachLine {
                try {
                    settings = Gson().fromJson(it, Settings::class.java)
                } catch (e: java.lang.Exception) {
                    e.message
                }
            }
        }
        Log.d(this::class.java.name, "Retrieved settings data from file.")
        return settings!!
    }


    companion object {
        private const val FILE_NAME = "/archerySessionSettings.txt"
        private val DIRECTORY: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val settingsFile = File(DIRECTORY, FILE_NAME)
    }

}