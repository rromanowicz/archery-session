package ex.rr.archerysession.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.gson.Gson
import com.rr.archerysession.R
import ex.rr.archerysession.data.Bow
import ex.rr.archerysession.data.DbSession
import ex.rr.archerysession.data.Session
import ex.rr.archerysession.data.Settings

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sessionQuery = ("CREATE TABLE IF NOT EXISTS " + SESSIONS_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                SESSION_COl + " TEXT" +
                ")")
        db.execSQL(sessionQuery)

        val settingsQuery = ("CREATE TABLE IF NOT EXISTS " + SETTINGS_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                SETTINGS_COl + " TEXT" +
                ")")
        db.execSQL(settingsQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $SESSIONS_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $SETTINGS_TABLE_NAME")
        onCreate(db)
    }

    fun addSession(session: Session): Long {
        val values = ContentValues()
        values.put(SESSION_COl, session.getJSON())
        val db = this.writableDatabase
        val savedItemId = db.insert(SESSIONS_TABLE_NAME, null, values)
        db.close()
        return savedItemId
    }

    fun addAll(sessions: MutableList<DbSession>): MutableList<Long> {
        val savedItems: MutableList<Long> = mutableListOf()
        val db = this.writableDatabase
        sessions.forEach {
            val values = ContentValues()
            values.put(ID_COL, it.id)
            values.put(SESSION_COl, it.session.getJSON())
            val savedItemId = db.insert(SESSIONS_TABLE_NAME, null, values)
            savedItems.add(savedItemId)
        }
        db.close()
        return savedItems
    }

    fun getAllSessions(): MutableList<DbSession> {
        return toDbSessionList(
            this.readableDatabase.rawQuery("SELECT * FROM $SESSIONS_TABLE_NAME", null)
        )
    }

    fun getXSessions(numberOfRows: String, offset: String = "0"): MutableList<DbSession> {
        return toDbSessionList(
            this.readableDatabase.rawQuery(
                "SELECT * FROM $SESSIONS_TABLE_NAME ORDER BY $ID_COL DESC LIMIT ? OFFSET ?",
                arrayOf(numberOfRows, offset)
            )
        )
    }

    fun getSessionById(id: String): MutableList<DbSession> {
        return toDbSessionList(
            this.readableDatabase.rawQuery(
                "SELECT * FROM $SESSIONS_TABLE_NAME WHERE $ID_COL=?",
                arrayOf(id)
            )
        )
    }

    fun getLastSessionId(): Long? {
        val cursor = this.readableDatabase.rawQuery(
            "SELECT $ID_COL FROM $SESSIONS_TABLE_NAME ORDER BY $ID_COL DESC LIMIT 1", null
        )
        return if (cursor.moveToNext()) cursor.getLong(0) else null
    }

    private fun toDbSessionList(cursor: Cursor?): MutableList<DbSession> {
        val sessionList: MutableList<DbSession> = mutableListOf()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    val idIndex = cursor.getColumnIndex(ID_COL)
                    val id = cursor.getLong(idIndex)
                    val jsonIndex = cursor.getColumnIndex(SESSION_COl)
                    val sessionJson = cursor.getString(jsonIndex)
                    val session = Gson().fromJson(sessionJson, Session::class.java)
                    sessionList.add(DbSession(id, session))
                } catch (e: Exception) {
                    Log.e(this::class.java.name, e.message!!)
                }
            }
        }
        Log.d(
            this::class.java.name,
            "Retrieved ${sessionList.size} records from database."
        )
        return sessionList
    }

    fun removeFromDb(sessionId: Long) {
        val db = this.writableDatabase
        db.delete(SESSIONS_TABLE_NAME, "$ID_COL = $sessionId", null)
        db.close()
        Log.d(
            this::class.java.name, "Session [$sessionId] removed from db."
        )
    }

    fun getSettings(): Settings? {
        return toSettings(
            this.readableDatabase.rawQuery("SELECT * FROM $SETTINGS_TABLE_NAME", null)
        )

    }

    private fun toSettings(cursor: Cursor?): Settings? {
        var settings: Settings? = null
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    val idIndex = cursor.getColumnIndex(ID_COL)
                    val id = cursor.getLong(idIndex)
                    val jsonIndex = cursor.getColumnIndex(SETTINGS_COl)
                    val settingsJson = cursor.getString(jsonIndex)
                    settings = Gson().fromJson(settingsJson, Settings::class.java)
                } catch (e: Exception) {
                    Log.e(this::class.java.name, e.message!!)
                }
            }
        }
        Log.d(
            this::class.java.name,
            "Retrieved settings from database."
        )
        return settings
    }

    private fun writeSettings(settings: Settings): Long {
        val values = ContentValues()
        values.put(SETTINGS_COl, settings.getJSON())
        Log.e("SETTINGS_DB", settings.getJSON())
        val db = this.writableDatabase
        db.delete(SETTINGS_TABLE_NAME, null, null)
        val savedItemId = db.insert(SETTINGS_TABLE_NAME, null, values)
        db.close()
        return savedItemId
    }

    fun addBow(bow: Bow): Int {
        var status: Int = 0
        var settings = getSettings()
        if (settings != null) {
            if (settings.bows.filter { it.name == bow.name }.isEmpty()) {
                bow.id = settings.bows.size.toLong().plus(1)
                settings.bows.add(bow)
            } else {
                status = 1
            }
        } else {
            settings = Settings(mutableListOf(bow))
        }
        writeSettings(settings)
        return status
    }

    fun removeBow(name: String) {
        val settings = getSettings()
        if (settings != null) {
            settings.bows = settings.bows.filter { it.name != name }.toMutableList()
            writeSettings(settings)
        }
    }

    fun getBowByName(name: String): Bow? {
        return getSettings()?.bows?.filter { it.name == name }?.firstOrNull()
    }


    fun init() {
        onCreate(this.writableDatabase)
    }

    companion object {
        private const val DATABASE_NAME = "ARCHERY_SESSIONS"
        private const val DATABASE_VERSION = 1
        private const val SESSIONS_TABLE_NAME = "sessions"
        private const val SETTINGS_TABLE_NAME = "settings"
        private const val ID_COL = "id"
        private const val SESSION_COl = "session_json"
        private const val SETTINGS_COl = "settings_json"
    }
}