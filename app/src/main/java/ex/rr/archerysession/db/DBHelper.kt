package ex.rr.archerysession.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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

    fun getAllSessions(): Cursor? {
        return this.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getXSessions(numberOfRows: String, offset: String= "0"): Cursor? {
        return this.readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_NAME ORDER BY $ID_COL DESC LIMIT ? OFFSET ?",
            arrayOf(numberOfRows, offset)
        )
    }

    companion object {
        private const val DATABASE_NAME = "ARCHERY_SESSIONS"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "sessions"
        const val ID_COL = "id"
        const val SESSION_COl = "session_json"
    }
}