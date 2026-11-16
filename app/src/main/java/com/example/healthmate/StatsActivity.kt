
package com.example.healthmate

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthmate.data.HealthDatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class StatsActivity : AppCompatActivity() {

    private lateinit var dbHelper: HealthDatabaseHelper

    private lateinit var tvAvgSteps: TextView
    private lateinit var tvAvgCalories: TextView
    private lateinit var tvAvgWater: TextView
    private lateinit var tvTotalRecords: TextView

    private lateinit var btnWeek: Button
    private lateinit var btnMonth: Button
    private lateinit var btnYear: Button

    private lateinit var bar1: View
    private lateinit var bar2: View
    private lateinit var bar3: View
    private lateinit var bar4: View
    private lateinit var bar5: View
    private lateinit var bar6: View
    private lateinit var bar7: View

    private lateinit var lbl1: TextView
    private lateinit var lbl2: TextView
    private lateinit var lbl3: TextView
    private lateinit var lbl4: TextView
    private lateinit var lbl5: TextView
    private lateinit var lbl6: TextView
    private lateinit var lbl7: TextView

    private lateinit var fabAdd: FloatingActionButton

    private var currentFilter = "Week"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        dbHelper = HealthDatabaseHelper(this)

        tvAvgSteps = findViewById(R.id.tvAvgSteps)
        tvAvgCalories = findViewById(R.id.tvAvgCalories)
        tvAvgWater = findViewById(R.id.tvAvgWater)
        tvTotalRecords = findViewById(R.id.tvTotalRecords)

        btnWeek = findViewById(R.id.btnWeek)
        btnMonth = findViewById(R.id.btnMonth)
        btnYear = findViewById(R.id.btnYear)

        bar1 = findViewById(R.id.bar1)
        bar2 = findViewById(R.id.bar2)
        bar3 = findViewById(R.id.bar3)
        bar4 = findViewById(R.id.bar4)
        bar5 = findViewById(R.id.bar5)
        bar6 = findViewById(R.id.bar6)
        bar7 = findViewById(R.id.bar7)

        lbl1 = findViewById(R.id.lbl1)
        lbl2 = findViewById(R.id.lbl2)
        lbl3 = findViewById(R.id.lbl3)
        lbl4 = findViewById(R.id.lbl4)
        lbl5 = findViewById(R.id.lbl5)
        lbl6 = findViewById(R.id.lbl6)
        lbl7 = findViewById(R.id.lbl7)

        fabAdd = findViewById(R.id.fabAdd)

        // Set Week as the default selected tab
        setActiveTab(btnWeek)
        loadStatistics(currentFilter)

        btnWeek.setOnClickListener {
            currentFilter = "Week"
            setActiveTab(btnWeek)
            loadStatistics(currentFilter)
        }

        btnMonth.setOnClickListener {
            currentFilter = "Month"
            setActiveTab(btnMonth)
            loadStatistics(currentFilter)
        }

        btnYear.setOnClickListener {
            currentFilter = "Year"
            setActiveTab(btnYear)
            loadStatistics(currentFilter)
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, RecordFormActivity::class.java))
        }

        // Bottom Bar Navigation

        findViewById<View>(R.id.navHomeLayout)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navStatsLayout)?.setOnClickListener {
            // Already in Stats screen
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

        // Keep the current selected tab and refresh its statistics
        if (::btnWeek.isInitialized) {
            when (currentFilter) {
                "Week" -> setActiveTab(btnWeek)
                "Month" -> setActiveTab(btnMonth)
                "Year" -> setActiveTab(btnYear)
            }

            loadStatistics(currentFilter)
        }
    }

    // Update tab backgrounds and text colours
    private fun setActiveTab(selectedButton: Button) {

        val activeColor = Color.parseColor("#10B981")
        val inactiveColor = Color.TRANSPARENT
        val inactiveTextColor = Color.parseColor("#64748B")

        val buttons = listOf(btnWeek, btnMonth, btnYear)

        for (button in buttons) {

            val isSelected = button == selectedButton

            val backgroundColor = if (isSelected) {
                activeColor
            } else {
                inactiveColor
            }

            val textColor = if (isSelected) {
                Color.WHITE
            } else {
                inactiveTextColor
            }

            // Create a fresh background to remove the previous green colour
            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12 * resources.displayMetrics.density
                setColor(backgroundColor)
            }

            // Replace the original background completely
            button.background = background

            // Apply the correct text colour
            button.setTextColor(textColor)

            button.isSelected = isSelected
        }
    }

    private fun loadStatistics(filter: String) {

        val records = dbHelper.getAllRecords()
        val totalCount = records.size

        if (totalCount > 0) {

            var totalSteps = 0
            var totalCalories = 0
            var totalWater = 0

            for (record in records) {
                totalSteps += record.steps
                totalCalories += record.calories
                totalWater += record.waterMl
            }

            val avgSteps = totalSteps / totalCount
            val avgCalories = totalCalories / totalCount
            val avgWater = totalWater / totalCount

            tvAvgSteps.text = avgSteps.toString()
            tvAvgCalories.text = "$avgCalories kcal"
            tvAvgWater.text = "$avgWater ml"
            tvTotalRecords.text = totalCount.toString()

            setBarHeight(bar1, 0)
            setBarHeight(bar2, 0)
            setBarHeight(bar3, 0)
            setBarHeight(bar4, 0)
            setBarHeight(bar5, 0)
            setBarHeight(bar6, 0)
            setBarHeight(bar7, 0)

            val dateFormat = SimpleDateFormat(
                "d MMMM yyyy",
                Locale.getDefault()
            )

            if (filter == "Week") {

                lbl1.text = "Mon"
                lbl2.text = "Tue"
                lbl3.text = "Wed"
                lbl4.text = "Thu"
                lbl5.text = "Fri"
                lbl6.text = "Sat"
                lbl7.text = "Sun"

                val dayFormat = SimpleDateFormat(
                    "EEE",
                    Locale.ENGLISH
                )

                for (record in records) {
                    try {
                        val cleanDateStr =
                            record.date.substringAfter(", ").trim()

                        val parsedDate = dateFormat.parse(cleanDateStr)

                        if (parsedDate != null) {

                            val dayOfWeek = dayFormat.format(parsedDate)

                            when (dayOfWeek) {
                                "Mon" -> setBarHeight(bar1, record.steps)
                                "Tue" -> setBarHeight(bar2, record.steps)
                                "Wed" -> setBarHeight(bar3, record.steps)
                                "Thu" -> setBarHeight(bar4, record.steps)
                                "Fri" -> setBarHeight(bar5, record.steps)
                                "Sat" -> setBarHeight(bar6, record.steps)
                                "Sun" -> setBarHeight(bar7, record.steps)
                            }
                        }

                    } catch (e: Exception) {
                        // Ignore invalid date
                    }
                }

            } else if (filter == "Month") {

                val monthFormat = SimpleDateFormat(
                    "MMM",
                    Locale.ENGLISH
                )

                val monthsList = mutableListOf<String>()

                for (record in records) {
                    try {
                        val cleanDateStr =
                            record.date.substringAfter(", ").trim()

                        val parsedDate = dateFormat.parse(cleanDateStr)

                        if (parsedDate != null) {
                            val mStr = monthFormat.format(parsedDate)

                            if (!monthsList.contains(mStr)) {
                                monthsList.add(mStr)
                            }
                        }

                    } catch (e: Exception) {
                        // Ignore invalid date
                    }
                }

                val labels = listOf(
                    lbl1, lbl2, lbl3, lbl4, lbl5, lbl6, lbl7
                )

                val bars = listOf(
                    bar1, bar2, bar3, bar4, bar5, bar6, bar7
                )

                for (i in labels.indices) {

                    if (i < monthsList.size) {
                        labels[i].text = monthsList[i]
                        setBarHeight(bars[i], records[i].steps)

                    } else {
                        labels[i].text = ""
                        setBarHeight(bars[i], 0)
                    }
                }

            } else if (filter == "Year") {

                val yearFormat = SimpleDateFormat(
                    "yyyy",
                    Locale.ENGLISH
                )

                val yearsList = mutableListOf<String>()

                for (record in records) {
                    try {
                        val cleanDateStr =
                            record.date.substringAfter(", ").trim()

                        val parsedDate = dateFormat.parse(cleanDateStr)

                        if (parsedDate != null) {
                            val yStr = yearFormat.format(parsedDate)

                            if (!yearsList.contains(yStr)) {
                                yearsList.add(yStr)
                            }
                        }

                    } catch (e: Exception) {
                        // Ignore invalid date
                    }
                }

                val labels = listOf(
                    lbl1, lbl2, lbl3, lbl4, lbl5, lbl6, lbl7
                )

                val bars = listOf(
                    bar1, bar2, bar3, bar4, bar5, bar6, bar7
                )

                for (i in labels.indices) {

                    if (i < yearsList.size) {
                        labels[i].text = yearsList[i]
                        setBarHeight(bars[i], records[i].steps)

                    } else {
                        labels[i].text = ""
                        setBarHeight(bars[i], 0)
                    }
                }
            }

        } else {

            tvAvgSteps.text = "0"
            tvAvgCalories.text = "0 kcal"
            tvAvgWater.text = "0 ml"
            tvTotalRecords.text = "0"

            setBarHeight(bar1, 0)
            setBarHeight(bar2, 0)
            setBarHeight(bar3, 0)
            setBarHeight(bar4, 0)
            setBarHeight(bar5, 0)
            setBarHeight(bar6, 0)
            setBarHeight(bar7, 0)
        }
    }

    private fun setBarHeight(barView: View, steps: Int) {

        val params = barView.layoutParams as LinearLayout.LayoutParams

        if (steps <= 0) {

            barView.visibility = View.INVISIBLE

        } else {

            barView.visibility = View.VISIBLE

            val maxHeightDp = 120

            val calculatedHeightDp =
                (minOf(steps, 10000) * maxHeightDp) / 10000

            val scale = resources.displayMetrics.density

            params.height = (calculatedHeightDp * scale).toInt()

            barView.layoutParams = params
        }
    }
}
