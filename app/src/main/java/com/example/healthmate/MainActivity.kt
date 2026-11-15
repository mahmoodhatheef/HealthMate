package com.example.healthmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.adapters.RecordsAdapter
import com.example.healthmate.data.HealthDatabaseHelper
import com.example.healthmate.models.HealthRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: HealthDatabaseHelper
    private lateinit var tvDate: TextView
    private lateinit var tvStepsCount: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvWater: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var btnViewAll: TextView
    private lateinit var btnRefresh: ImageView
    private lateinit var rvRecentRecords: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = HealthDatabaseHelper(this)

        tvDate = findViewById(R.id.tvDate)
        tvStepsCount = findViewById(R.id.tvStepsCount)
        tvCalories = findViewById(R.id.tvCalories)
        tvWater = findViewById(R.id.tvWater)
        fabAdd = findViewById(R.id.fabAdd)
        btnViewAll = findViewById(R.id.btnViewAll)
        btnRefresh = findViewById(R.id.btnRefresh)
        rvRecentRecords = findViewById(R.id.rvRecentRecords)

        rvRecentRecords.layoutManager = LinearLayoutManager(this)

        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(Date())

        fabAdd.setOnClickListener {
            startActivity(Intent(this, RecordFormActivity::class.java))
        }

        btnViewAll.setOnClickListener {
            startActivity(Intent(this, RecordsListActivity::class.java))
        }

        btnRefresh.setOnClickListener {
            loadDashboardData()
        }

        findViewById<View>(R.id.navHomeLayout)?.setOnClickListener { }
        findViewById<View>(R.id.navStatsLayout)?.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.navRecordsLayout)?.setOnClickListener {
            startActivity(Intent(this, RecordsListActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.navSettingsLayout)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val records = dbHelper.getAllRecords()
        if (records.isNotEmpty()) {
            val latest = records[0]
            tvStepsCount.text = latest.steps.toString()
            tvCalories.text = "${latest.calories} kcal"
            tvWater.text = "${latest.waterMl} ml"

            val recentList = records.take(3)
            val adapter = RecordsAdapter(
                recentList,
                onEditClick = { record: HealthRecord ->
                    val intent = Intent(this, RecordFormActivity::class.java)
                    intent.putExtra("RECORD_ID", record.id)
                    intent.putExtra("RECORD_DATE", record.date)
                    intent.putExtra("RECORD_STEPS", record.steps)
                    intent.putExtra("RECORD_CALORIES", record.calories)
                    intent.putExtra("RECORD_WATER", record.waterMl)
                    startActivity(intent)
                },
                onDeleteClick = { record: HealthRecord ->
                    AlertDialog.Builder(this)
                        .setTitle("Delete Record")
                        .setMessage("Are you sure you want to delete this record?")
                        .setPositiveButton("Yes") { _, _ ->
                            dbHelper.deleteRecord(record.id)
                            loadDashboardData()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            )
            rvRecentRecords.adapter = adapter
        } else {
            tvStepsCount.text = "0"
            tvCalories.text = "0 kcal"
            tvWater.text = "0 ml"
            rvRecentRecords.adapter = null
        }
    }
}