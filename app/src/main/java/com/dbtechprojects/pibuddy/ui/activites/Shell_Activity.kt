package com.dbtechprojects.pibuddy.ui.activites

import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.databinding.ActivityShellBinding
import com.dbtechprojects.pibuddy.models.Connection
import com.dbtechprojects.pibuddy.ui.viewmodels.ShellViewModel
import com.dbtechprojects.pibuddy.ui.viewmodels.ShellViewModelFactory
import com.dbtechprojects.pibuddy.utilities.Constants
import com.dbtechprojects.pibuddy.utilities.Resource

class Shell_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityShellBinding
    private  val viewModel : ShellViewModel by viewModels {
        ShellViewModelFactory(intent.getParcelableExtra<Connection>("Connection")!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShellBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClicks()

        intent.getParcelableExtra<Connection>("Connection")?.let {
            setupActionBar(it.ipAddress)
        }

        initObserver()
    }

    private fun initObserver() {
        viewModel.commandOutput.observe(this, { commandOutput ->
            when(commandOutput){
                is Resource.Success -> commandOutput.data?.let {
                    updateShell(it)
                    enableSend(true)
                }
                is Resource.Error -> commandOutput.error?.let {
                    updateShell(it)
                    enableSend(true)
                }
            }
        })
    }

    private fun updateShell(newText: String){
        binding.shellWindow.text = binding.shellWindow.text.toString().replace(Constants.WAITING_MESSAGE, newText)
    }

    private fun setupClicks() {
        binding.shellWindow.movementMethod = ScrollingMovementMethod()
        binding.sendCommandBtn.setOnClickListener {
            if (binding.commandInput.text.trim().isEmpty()) {
                Toast.makeText(this, "input is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.shellWindow.append("\n" +  "> " + binding.commandInput.text)
            binding.shellWindow.append("\n")
            binding.shellWindow.append(Constants.setColorForText(Constants.WAITING_MESSAGE, Color.WHITE))
            viewModel.sendCommand(binding.commandInput.text.toString())
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
            actionBar.title = "Command@" + IP
        }

        binding.toolbar2.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pi_buddy_shell_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.toolbar_menu_shell -> {
                binding.shellWindow.text = ""
                binding.shellWindow.scrollTo(0,0)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectSession()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.disconnectSession()
        finish()
    }
}