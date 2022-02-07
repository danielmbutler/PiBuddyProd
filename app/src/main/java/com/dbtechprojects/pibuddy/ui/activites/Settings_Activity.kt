package com.dbtechprojects.pibuddy.ui.activites


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.databinding.ActivityMainBinding
import com.dbtechprojects.pibuddy.databinding.ActivitySettingsBinding
import com.dbtechprojects.pibuddy.dialogs.ChangePortDialog
import com.dbtechprojects.pibuddy.dialogs.HelpDialog

class Settings_Activity : AppCompatActivity() {
    private  var _binding: ActivitySettingsBinding? = null
    val binding: ActivitySettingsBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        binding.SettingsHelpButton.setOnClickListener {
            val dialog =
                HelpDialog()
            dialog.show(supportFragmentManager, "Help")
        }
        binding.SettingsChangePortButton.setOnClickListener {
            val dialog =
                ChangePortDialog()
            dialog.show(supportFragmentManager, "ChangePort")
        }

    }


    private fun setupActionBar() {

        setSupportActionBar(binding.settingsToolBar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24_white)
            actionBar.title = getString(R.string.settings)
        }

        binding.settingsToolBar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}