package ex.rr.archerysession.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import ex.rr.archerysession.data.DbSession
import ex.rr.archerysession.data.Session

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                SESSION_COl + " TEXT" +
                ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addSession(session: Session): Long {
        val values = ContentValues()
        values.put(SESSION_COl, session.getJSON())
        val db = this.writableDatabase
        val savedItemId = db.insert(TABLE_NAME, null, values)
        db.close()
        return savedItemId
    }

    fun addAll(sessions: MutableMap<Long, DbSession>): MutableList<Long> {
        val savedItems: MutableList<Long> = mutableListOf()
        val db = this.writableDatabase
        sessions.forEach {
            val values = ContentValues()
            values.put(ID_COL, it.key)
            values.put(SESSION_COl, it.value.session.getJSON())
            val savedItemId = db.insert(TABLE_NAME, null, values)
            savedItems.add(savedItemId)
        }
        db.close()
        return savedItems
    }

    fun getAllSessions(): MutableList<DbSession> {
        return toDbSessionList(
            this.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
        )
    }

    fun getXSessions(numberOfRows: String, offset: String = "0"): MutableList<DbSession> {
        return toDbSessionList(
            this.readableDatabase.rawQuery(
                "SELECT * FROM $TABLE_NAME ORDER BY $ID_COL DESC LIMIT ? OFFSET ?",
                arrayOf(numberOfRows, offset)
            )
        )
    }

    fun getSessionById(id: String): MutableList<DbSession> {
        return toDbSessionList(
            this.readableDatabase.rawQuery(
                "SELECT * FROM $TABLE_NAME WHERE $ID_COL=?",
                arrayOf(id)
            )
        )
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
            this::class.java.canonicalName,
            "Retrieved ${sessionList.size} records from database."
        )
        return sessionList
    }

    companion object {
        private const val DATABASE_NAME = "ARCHERY_SESSIONS"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "sessions"
        private const val ID_COL = "id"
        private const val SESSION_COl = "session_json"
    }
}