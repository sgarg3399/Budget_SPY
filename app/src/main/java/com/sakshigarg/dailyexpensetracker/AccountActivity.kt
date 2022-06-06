package com.sakshigarg.dailyexpensetracker

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

import android.content.DialogInterface
import android.view.View


class AccountActivity : AppCompatActivity() {

    private lateinit var settingsToolbar:androidx.appcompat.widget.Toolbar
    private lateinit var userEmail:TextView
    private lateinit var logoutbtn:AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        settingsToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(settingsToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setTitle("My Account")
        settingsToolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })

        logoutbtn = findViewById(R.id.logoutBtn)
        userEmail = findViewById(R.id.userEmail)
        userEmail.setText(FirebaseAuth.getInstance().currentUser!!.email)

        logoutbtn.setOnClickListener {
            AlertDialog.Builder(this@AccountActivity)
                .setTitle("Budget SPY")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@AccountActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }).setNegativeButton("No", null)
                .show()
        }
    }
}