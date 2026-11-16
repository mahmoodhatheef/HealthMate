package com.example.healthmate.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.healthmate.models.HealthRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HealthDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "healthmate.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "health_records"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_STEPS = "steps"
        private const val COLUMN_CALORIES = "calories"
        private const val COLUMN_WATER = "water"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_DATE TEXT UNIQUE, " +
                        "$COLUMN_STEPS INTEGER, " +
                        "$COLUMN_CALORIES INTEGER, " +
                        "$COLUMN_WATER INTEGER)"
                )

        db?.execSQL(createTableQuery)
        db?.let { insertDummyRecords(it) }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Dummy records for initial testing (last 7 days, oldest first)
    private fun insertDummyRecords(db: SQLiteDatabase) {
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        // steps, calories, water (ml)
        val sampleData = listOf(
            Triple(6200, 340, 1500),
            Triple(8400, 450, 2200),
            Triple(5100, 290, 1800),
            Triple(9300, 510, 2500),
            Triple(7000, 380, 1900),
            Triple(10200, 560, 2600),
            Triple(7850, 420, 2000)
        )

        for ((steps, calories, water) in sampleData) {
            val values = ContentValues().apply {
                put(COLUMN_DATE, dateFormat.format(calendar.time))
                put(COLUMN_STEPS, steps)
                put(COLUMN_CALORIES, calories)
                put(COLUMN_WATER, water)
            }
            db.insert(TABLE_NAME, null, values)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    fun addRecord(record: HealthRecord): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_DATE, record.date)
            put(COLUMN_STEPS, record.steps)
            put(COLUMN_CALORIES, record.calories)
            put(COLUMN_WATER, record.waterMl)
        }

        val result = db.insertWithOnConflict(
            TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        return result != -1L
    }

    fun updateRecord(record: HealthRecord): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_DATE, record.date)
            put(COLUMN_STEPS, record.steps)
            put(COLUMN_CALORIES, record.calories)
            put(COLUMN_WATER, record.waterMl)
        }

        val rows = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(record.id.toString())
        )

        return rows > 0
    }

    fun deleteRecord(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Delete all health records
    fun deleteAllRecords(): Boolean {
        val db = writableDatabase

        return try {
            db.delete(TABLE_NAME, null, null) > 0
        } catch (e: Exception) {
            false
        }
    }

    fun getAllRecords(): List<HealthRecord> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC",
            null
        )
        return readRecords(cursor)
    }

    fun searchRecords(query: String): List<HealthRecord> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE LIKE ? ORDER BY $COLUMN_ID DESC",
            arrayOf("%$query%")
        )
        return readRecords(cursor)
    }

    private fun readRecords(cursor: Cursor): List<HealthRecord> {
        val records = mutableListOf<HealthRecord>()

        cursor.use {
            while (it.moveToNext()) {
                records.add(
                    HealthRecord(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                        steps = it.getInt(it.getColumnIndexOrThrow(COLUMN_STEPS)),
                        calories = it.getInt(it.getColumnIndexOrThrow(COLUMN_CALORIES)),
                        waterMl = it.getInt(it.getColumnIndexOrThrow(COLUMN_WATER))
                    )
                )
            }
        }
        return records
    }
}