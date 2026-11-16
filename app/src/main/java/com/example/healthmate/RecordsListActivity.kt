package com.example.healthmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.adapters.RecordsAdapter
import com.example.healthmate.models.HealthRecord
import com.example.healthmate.viewmodel.HealthViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RecordsListActivity : AppCompatActivity() {

    private val viewModel: HealthViewModel by viewModels()

    private lateinit var rvRecords: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_list)

        rvRecords = findViewById(R.id.rvRecords)
        etSearch = findViewById(R.id.etSearch)
        fabAdd = findViewById(R.id.fabAdd)

        rvRecords.layoutManager = LinearLayoutManager(this)

        // Observe records from the ViewModel and refresh the list
        viewModel.records.observe(this) { records ->
            showRecords(records)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.loadRecords(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        fabAdd.setOnClickListener {
            startActivity(Intent(this, RecordFormActivity::class.java))
        }

        findViewById<View>(R.id.navHomeLayout)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.navStatsLayout)?.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.navRecordsLayout)?.setOnClickListener { }
        findViewById<View>(R.id.navSettingsLayout)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRecords(etSearch.text.toString().trim())
    }

    private fun showRecords(records: List<HealthRecord>) {
        rvRecords.adapter = RecordsAdapter(
            records,
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
                        viewModel.deleteRecord(record.id)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )
    }
}