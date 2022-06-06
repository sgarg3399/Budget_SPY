package com.sakshigarg.dailyexpensetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView

class ChooseAnalyticActivity : AppCompatActivity() {
    private lateinit var todayCardView:CardView
    private lateinit var weekCardView:CardView
    private lateinit var monthCardView:CardView
    private lateinit var toolbars: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_analytic)

        toolbars = findViewById(R.id.toolbar)
        setSupportActionBar(toolbars)
        supportActionBar?.title = "Choose Analytics"

        todayCardView = findViewById(R.id.todayCardView)
        weekCardView = findViewById(R.id.weekCardView)
        monthCardView = findViewById(R.id.monthCardView)

        todayCardView.setOnClickListener {
            startActivity(Intent(this, DailyAnalyticsActivity::class.java))
        }

        weekCardView.setOnClickListener {
            startActivity(Intent(this, WeeklyAnalyticsActivity::class.java))
        }
        monthCardView.setOnClickListener {
            startActivity(Intent(this, MonthlyAnalyticsActivity::class.java))
        }
    }
}