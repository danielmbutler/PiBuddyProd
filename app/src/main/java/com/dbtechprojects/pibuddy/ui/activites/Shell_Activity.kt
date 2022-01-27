package com.dbtechprojects.pibuddy.ui.activites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Shell_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.onDestroy()
    }
}