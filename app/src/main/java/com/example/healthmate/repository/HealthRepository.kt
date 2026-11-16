package com.example.healthmate.repository

import android.content.Context
import com.example.healthmate.data.HealthDatabaseHelper
import com.example.healthmate.models.HealthRecord

class HealthRepository(context: Context) {

    private val dbHelper = HealthDatabaseHelper(context.applicationContext)

    fun getAllRecords(): List<HealthRecord> = dbHelper.getAllRecords()

    fun searchRecords(query: String): List<HealthRecord> = dbHelper.searchRecords(query)

    fun addRecord(record: HealthRecord): Boolean = dbHelper.addRecord(record)

    fun updateRecord(record: HealthRecord): Boolean = dbHelper.updateRecord(record)

    fun deleteRecord(id: Int) = dbHelper.deleteRecord(id)

    fun deleteAllRecords(): Boolean = dbHelper.deleteAllRecords()
}