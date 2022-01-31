package com.dbtechprojects.pibuddy.ui.activites

import DeploymentAdapter
import DeploymentResult
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.databinding.ActivityDeploymentBinding
import com.dbtechprojects.pibuddy.dialogs.DeployOutputDialog
import com.dbtechprojects.pibuddy.dialogs.HelpDialog
import com.dbtechprojects.pibuddy.models.Connection
import com.dbtechprojects.pibuddy.models.findByIp
import com.dbtechprojects.pibuddy.ui.viewmodels.DeploymentViewModel
import com.dbtechprojects.pibuddy.utilities.Resource
import com.dbtechprojects.pibuddy.utilities.SharedPref
import findByIp
import org.json.JSONObject

class Deployment_Activity : AppCompatActivity(), DeploymentAdapter.OnClickListener {

    private var _binding : ActivityDeploymentBinding? = null
    private val binding : ActivityDeploymentBinding get() = _binding!!
    private lateinit var pref: SharedPreferences
    private lateinit var adapter: DeploymentAdapter
    private val viewModel: DeploymentViewModel by viewModels()
    private val deploymentDevices: MutableList<DeploymentResult> = mutableListOf()
    private val savedDevices: MutableList<Connection> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDeploymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        pref = SharedPref(this).sharedPreferences


        getAllDevices()
        setupRV()
        setupClicks()
        initObservers()
    }

    private fun setupClicks() {
        binding.runCommandBtn.setOnClickListener { deployScript()}
    }

    private fun getAllDevices(){
        //get all preferences
        binding.progressBar.visibility = View.VISIBLE
        val keys: MutableMap<String, *> = pref.all

        for ((key, value) in keys) {
            //Log.d("onclick listner", key)
            pref.getString(this.title.toString(), null)
            val strJson = pref.getString(key, null)

            val jresponse = JSONObject(strJson)
            val UsernameFromJson = jresponse.getString("Username")
            val PasswordFromJson = jresponse.getString("Password")
            savedDevices.add(Connection(ipAddress = key, username = UsernameFromJson, password = PasswordFromJson))
        }

        savedDevices.forEach { viewModel.testDevice(it.ipAddress) }
    }

    private fun initObservers(){
        viewModel.ips.observe(this, { pingResult ->
            // ping result only returns Resource.Success
            when(pingResult){
                is Resource.Success -> {
                    pingResult.data?.let {
                        DeploymentResult(
                            ip = it.ipAddress,
                            connected = it.result
                        )
                    }?.let { deploymentDevices.add(it) }
                }
            }
            binding.progressBar.visibility = View.GONE
            refreshRecyclerViewMessages()
        })

        viewModel.commandResults.observe(this, { commandResult ->
            val index = adapter.getDevices().findByIp(commandResult.ip)
            if (index != null) {
                adapter.updateDeviceAtIndex(DeploymentResult(output = commandResult.result, commandResult.ip, true), index)
            }
            binding.progressBar.visibility = View.GONE
            binding.runCommandBtn.isEnabled = true
        })
    }

    private fun deployScript(){
        val script = binding.deploymentCommandInput.text.trim().toString()
        if (script.isEmpty()){
            Toast.makeText(this, "command is empty", Toast.LENGTH_SHORT).show()
            return
        }
        val checkedDevices = adapter.getCheckedIps()

        if (checkedDevices.isEmpty()){
            Toast.makeText(this, "please select a device if you wish to run a script", Toast.LENGTH_SHORT).show()
            return
        }

        // run command
        checkedDevices.forEach {
            savedDevices.findByIp(it)?.let { viewModel.runCommand(it, script) }
        }
        binding.runCommandBtn.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setupRV(){
        adapter = DeploymentAdapter(this)
        binding.recyclerviewDeployment.adapter = adapter
        // faint line underneath each row
        binding.recyclerviewDeployment.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun refreshRecyclerViewMessages(){
        adapter.setList(deploymentDevices)
    }

    override fun onClick(item: DeploymentResult) {
        item.output?.let {
            if (it.isNotEmpty()){
                val dialog =
                    DeployOutputDialog(it)
                dialog.show(supportFragmentManager, "Deploy")
            }
        }

    }

    private fun setupToolbar(){
        setSupportActionBar(binding.toolbarDeployment)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24_white)
            actionBar.title = "Script Deployment"
        }

        binding.toolbarDeployment.setNavigationOnClickListener { onBackPressed() }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.commandResults.removeObservers(this)
        viewModel.ips.removeObservers(this)
    }
}

