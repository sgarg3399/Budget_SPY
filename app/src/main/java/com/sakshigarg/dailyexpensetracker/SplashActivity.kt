package com.sakshigarg.dailyexpensetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class SplashActivity : AppCompatActivity() {


    private lateinit var applogo:ImageView
    private lateinit var appName: TextView
    private lateinit var splashanimation:Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        splashanimation= AnimationUtils.loadAnimation(this, R.anim.animation)

        applogo= findViewById(R.id.logo)
        appName= findViewById(R.id.app_name)

        applogo.animation =splashanimation

         Handler().postDelayed({
             val Intent= Intent(this,LoginActivity::class.java)
             startActivity(Intent)
             finish()
         },3000)
    }
}