package com.example.healthmate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthmate.data.HealthDatabaseHelper
import com.example.healthmate.models.HealthRecord

class RecordFormActivity : AppCompatActivity() {

    private lateinit var dbHelper: HealthDatabaseHelper
    private lateinit var etDate: EditText
    private lateinit var etSteps: EditText
    private lateinit var etCalories: EditText
    private lateinit var etWater: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView

    private var recordId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_form)

        dbHelper = HealthDatabaseHelper(this)

        etDate = findViewById(R.id.etDate)
        etSteps = findViewById(R.id.etSteps)
        etCalories = findViewById(R.id.etCalories)
        etWater = findViewById(R.id.etWater)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // Check if editing existing record
        if (intent.hasExtra("RECORD_ID")) {
            recordId = intent.getIntExtra("RECORD_ID", -1)
            etDate.setText(intent.getStringExtra("RECORD_DATE"))
            etSteps.setText(intent.getIntExtra("RECORD_STEPS", 0).toString())
            etCalories.setText(intent.getIntExtra("RECORD_CALORIES", 0).toString())
            etWater.setText(intent.getIntExtra("RECORD_WATER", 0).toString())
            btnSave.text = "Update Record"
        }

        btnSave.setOnClickListener {
            val dateStr = etDate.text.toString().trim()
            val stepsStr = etSteps.text.toString().trim()
            val caloriesStr = etCalories.text.toString().trim()
            val waterStr = etWater.text.toString().trim()

            if (dateStr.isEmpty() || stepsStr.isEmpty() || caloriesStr.isEmpty() || waterStr.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val steps = stepsStr.toIntOrNull()
            val calories = caloriesStr.toIntOrNull()
            val water = waterStr.toIntOrNull()

            if (steps == null || steps < 0) {
                Toast.makeText(this, "Steps must be a valid positive number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (calories == null || calories < 0) {
                Toast.makeText(this, "Calories must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (water == null || water < 0) {
                Toast.makeText(this, "Water must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (recordId == -1) {
                // Add new record
                val record = HealthRecord(date = dateStr, steps = steps, calories = calories, waterMl = water)
                val success = dbHelper.addRecord(record)
                if (success) {
                    Toast.makeText(this, "Record saved successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "A record for this date already exists!", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update existing record
                val record = HealthRecord(id = recordId, date = dateStr, steps = steps, calories = calories, waterMl = water)
                val success = dbHelper.updateRecord(record)
                if (success) {
                    Toast.makeText(this, "Record updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update record", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}