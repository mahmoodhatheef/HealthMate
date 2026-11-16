package com.example.healthmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthmate.models.HealthRecord
import com.example.healthmate.repository.HealthRepository

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthRepository(application)

    private val _records = MutableLiveData<List<HealthRecord>>(emptyList())
    val records: LiveData<List<HealthRecord>> = _records

    private var currentQuery = ""

    fun loadRecords(query: String = "") {
        currentQuery = query
        _records.value =
            if (query.isEmpty()) repository.getAllRecords()
            else repository.searchRecords(query)
    }

    fun addRecord(record: HealthRecord): Boolean {
        val success = repository.addRecord(record)
        if (success) loadRecords(currentQuery)
        return success
    }

    fun updateRecord(record: HealthRecord): Boolean {
        val success = repository.updateRecord(record)
        if (success) loadRecords(currentQuery)
        return success
    }

    fun deleteRecord(id: Int) {
        repository.deleteRecord(id)
        loadRecords(currentQuery)
    }

    fun deleteAllRecords(): Boolean {
        val success = repository.deleteAllRecords()
        loadRecords(currentQuery)
        return success
    }
}