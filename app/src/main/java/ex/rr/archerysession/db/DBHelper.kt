package ex.rr.archerysession.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import ex.rr.archerysession.data.Bow
import ex.rr.archerysession.data.DbSession
import ex.rr.archerysession.data.Session

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sessionQuery = ("CREATE TABLE IF NOT EXISTS " + SESSIONS_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                SESSION_COl + " TEXT" +
                ")")
        db.execSQL(sessionQuery)

        val targetDistanceQuery = ("CREATE TABLE IF NOT EXISTS " + TARGET_DISTANCE_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                TARGET_DISTANCE_COL + " TEXT" +
                ")")
        db.execSQL(targetDistanceQuery)

        val bowsQuery = ("CREATE TABLE IF NOT EXISTS " + BOWS_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, "
                + NAME_COL + " TEXT UNIQUE, "
                + DESCRIPTION_COL + " TEXT, "
                + LENGTH_COL + " INTEGER, "
                + BRACE_HEIGHT_COL + " LONG, "
                + TOP_TILLER_COL + " LONG, "
                + BOTTOM_TILLER_COL + " LONG "
                + ")")
        db.execSQL(bowsQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $SESSIONS_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $BOWS_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TARGET_DISTANCE_TABLE_NAME")
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
        val sessionId = if (cursor.moveToNext()) cursor.getLong(0) else null
        cursor.close()
        return sessionId
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
                    session.mapScoresToScoreMap()
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

    fun getBows(): List<Bow> {
        val cursor = this.readableDatabase.rawQuery("SELECT * FROM $BOWS_TABLE_NAME", null)
        val bows: MutableList<Bow> = mutableListOf()

        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    val idIndex = cursor.getColumnIndex(ID_COL)
                    val id = cursor.getLong(idIndex)
                    val nameIndex = cursor.getColumnIndex(NAME_COL)
                    val name = cursor.getString(nameIndex)
                    val descriptionIndex = cursor.getColumnIndex(DESCRIPTION_COL)
                    val description = cursor.getString(descriptionIndex)
                    val lengthIndex = cursor.getColumnIndex(LENGTH_COL)
                    val length = cursor.getInt(lengthIndex)
                    val braceHeightIndex = cursor.getColumnIndex(BRACE_HEIGHT_COL)
                    val braceHeight = cursor.getLong(braceHeightIndex)
                    val topTillerIndex = cursor.getColumnIndex(TOP_TILLER_COL)
                    val topTiller = cursor.getLong(topTillerIndex)
                    val bottomTillerIndex = cursor.getColumnIndex(BOTTOM_TILLER_COL)
                    val bottomTiller = cursor.getLong(bottomTillerIndex)

                    bows.add(
                        Bow(id, name, description, length, braceHeight, topTiller, bottomTiller)
                    )
                } catch (e: Exception) {
                    Log.e(this::class.java.name, e.message!!)
                }
            }
        }
        cursor.close()
        return bows
    }

    fun addBow(bow: Bow): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        if (bow.id != 0L) {
            values.put(ID_COL, bow.id)
        }
        values.put(NAME_COL, bow.name)
        values.put(DESCRIPTION_COL, bow.description)
        values.put(LENGTH_COL, bow.length)
        values.put(BRACE_HEIGHT_COL, bow.braceHeight)
        values.put(TOP_TILLER_COL, bow.topTiller)
        values.put(BOTTOM_TILLER_COL, bow.bottomTiller)

        return if (bow.id != 0L) {
            db.update(BOWS_TABLE_NAME, values, "$ID_COL=?", arrayOf(bow.id.toString()))
        } else {
            db.insert(BOWS_TABLE_NAME, null, values).toInt()
        }
    }

    fun removeBow(id: Long) {
        this.writableDatabase.delete(BOWS_TABLE_NAME, "$ID_COL=?", arrayOf(id.toString()))
    }

    fun getBowByName(name: String): Bow? {
        return getBows().firstOrNull { it.name == name }
    }

    fun addTargetDistance(distance: String): Long {
        val values = ContentValues()
        values.put(TARGET_DISTANCE_COL, distance)
        val db = this.writableDatabase
        val savedItemId = db.insert(TARGET_DISTANCE_TABLE_NAME, null, values)
        db.close()
        return savedItemId
    }

    fun removeTargetDistance(distance: String) {
        val db = this.writableDatabase
        db.delete(TARGET_DISTANCE_TABLE_NAME, "$TARGET_DISTANCE_COL = $distance", null)
        db.close()
    }

    fun getTargetDistanceList(): MutableList<String> {
        return toTargetDistanceList(
            this.readableDatabase.rawQuery("SELECT * FROM $TARGET_DISTANCE_TABLE_NAME", null)
        )
    }

    private fun toTargetDistanceList(cursor: Cursor?): MutableList<String> {
        val targetDistanceList: MutableList<String> = mutableListOf()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    val index = cursor.getColumnIndex(TARGET_DISTANCE_COL)
                    val value = cursor.getString(index)
                    targetDistanceList.add(value)
                } catch (e: Exception) {
                    Log.e(this::class.java.name, e.message!!)
                }
            }
        }
        Log.d(
            this::class.java.name,
            "Retrieved ${targetDistanceList.size} records from database."
        )
        return targetDistanceList
    }

    fun init() {
        onCreate(this.writableDatabase)
    }

    companion object {
        private const val DATABASE_NAME = "ARCHERY_SESSIONS"
        private const val DATABASE_VERSION = 1
        private const val SESSIONS_TABLE_NAME = "sessions"
        private const val BOWS_TABLE_NAME = "bows"
        private const val TARGET_DISTANCE_TABLE_NAME = "target_distance"
        private const val ID_COL = "id"
        private const val SESSION_COl = "session_json"
        private const val NAME_COL = "name"
        private const val DESCRIPTION_COL = "description"
        private const val LENGTH_COL = "length"
        private const val BRACE_HEIGHT_COL = "brace_height"
        private const val TOP_TILLER_COL = "top_tiller"
        private const val BOTTOM_TILLER_COL = "bottom_tiller"
        private const val TARGET_DISTANCE_COL = "target_distance"
    }
}