package com.example.pibuddy.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.pibuddy.R

class Splash_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_)

        Handler().postDelayed(
            {
                // Launch the Main Activity
                startActivity(Intent(this@Splash_Activity, MainActivity::class.java))
                finish() // Call this when your activity is done and should be closed.
            },
            2500
        ) //
    }
}