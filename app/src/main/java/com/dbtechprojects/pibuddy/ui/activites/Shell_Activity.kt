package com.dbtechprojects.pibuddy.ui.activites

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.databinding.ActivityShellBinding
import com.dbtechprojects.pibuddy.utilities.Constants

class Shell_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityShellBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShellBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClicks()

        intent.getStringExtra("IPAddress")?.let {
            setupActionBar(it)
        }
    }

    private fun setupClicks() {
        binding.sendCommandBtn.setOnClickListener {
            if (binding.commandInput.text.trim().isEmpty()) {
                Toast.makeText(this, "input is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.shellWindow.append("\n" +  "> " + binding.commandInput.text)
            binding.shellWindow.append("\n")
            binding.shellWindow.append(Constants.setColorForText("waiting....", Color.WHITE))
            enableSend(false)
        }
    }

    private fun enableSend(shouldSend: Boolean){
        binding.sendCommandBtn.text = if(shouldSend)"SEND" else "sending"
        binding.sendCommandBtn.isEnabled = shouldSend
    }

    private fun setupActionBar(IP: String) {

        setSupportActionBar(binding.toolbar2)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24_white)
            actionBar.title = "Shell@" + IP
        }

        binding.toolbar2.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}