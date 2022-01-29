package com.dbtechprojects.pibuddy.ui.activites

import ScanAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.dbtechprojects.pibuddy.dialogs.HelpDialog
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.databinding.ActivityScanBinding
import com.dbtechprojects.pibuddy.ui.viewmodels.ScanViewModel
import com.dbtechprojects.pibuddy.utilities.NetworkUtils
import com.dbtechprojects.pibuddy.utilities.Resource
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.*
import org.apache.commons.net.util.SubnetUtils
import java.lang.NullPointerException


class Scan_Activity : AppCompatActivity() {
    companion object{
        val TAG = "Scan_Activity"
    }

    private var cancelled = false
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val IPs: MutableList<String> = mutableListOf()
    private var clientAddress = "none"


    private val viewModel: ScanViewModel by viewModels()
    private lateinit var binding: ActivityScanBinding
    

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRV()
        setupClicks()


        // check for Wifi Address
        //get IP and subnet mask (comes out in CIDR *.*.*.*/*)
        // verify network connectivity
        getClientWifiAddress(false)



    }

    private fun getClientWifiAddress(refresh: Boolean) {
        try{
            val connectivityManager =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val addresses = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)!!.linkAddresses
            addresses.forEach {
                if(NetworkUtils.validate(it.address.toString().replace("/",""))){
                    //Log.d("wifi", "${it.toString()} validated")
                    clientAddress = it.toString()
                }
            }
        } catch (ce: NullPointerException){

            Toast.makeText(this@Scan_Activity, "Wifi Connection Not Found, Please check Wifi", Toast.LENGTH_LONG).show()
            binding.ScanningTextView.text = resources.getString(R.string.returnwifiText)
        }

        if(clientAddress == "none"){
            Toast.makeText(this@Scan_Activity, "no Wifi found please check Wifi", Toast.LENGTH_LONG).show()
        } else {
            if (!refresh)initScanIpObserver()
            val netAddresses = networkScanIP(clientAddress)
            // scan IPS and confirm activity
            viewModel.scanIPs(netAddresses)

            
        }
    }

    private fun initScanIpObserver() {
        viewModel.ips.observe(this, Observer { ip ->
            //add IP to IP list
            when(ip){
                is Resource.Success -> {
                    ip.data?.let { IPs.add(it.ipAddress)
                        refreshRecyclerViewMessages()
                    }
                }
            }


        })

        viewModel.addressCount.observe(this, Observer { count ->
            // update text view
            Log.d(TAG, "addresscount : $count ")
            val addtext = "Scanning for Devices with port 22 open ...... $count addresses remaining"

            // if scan is not cancelled set text
            if (!cancelled){
                binding.ScanningTextView.text = addtext
            }

        })
    }

    private fun setupClicks() {

        binding.ScanStopButton.setOnClickListener {
            if (!cancelled){
                cancelled = true
                viewModel.cancelScan()
                val messagetext = "Scan Stopped"
                binding.ScanningTextView.text = messagetext

                binding.ScanStopButton.text = getString(R.string.RestartScan)
            } else {
                getClientWifiAddress(true)
                IPs.clear()
                refreshRecyclerViewMessages()
                cancelled = false
                binding.ScanStopButton.text = getString(R.string.stop_scan)
            }
        }

        binding.ScanBackButton.setOnClickListener {
            viewModel.cancelScan()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun setupRV(){

        adapter.setOnItemClickListener{ item: Item<GroupieViewHolder>, view: View ->

            val IP = item as ScanAdapter
            //Log.d(TAG, IP.IP)

            intent= Intent(this@Scan_Activity,
                MainActivity::class.java)

            intent.putExtra("IPAddress", IP.IP)

            startActivity(intent)

            viewModel.cancelScan()
            finish()


        }
        binding.ScanViewRecyclerView.adapter = adapter

        // faint line underneath each row
        binding.ScanViewRecyclerView.addItemDecoration(DividerItemDecoration(this@Scan_Activity, DividerItemDecoration.VERTICAL))
    }

    private fun refreshRecyclerViewMessages(){
        println("RecyclerviewRefresh called + $IPs")
        adapter.clear()
        IPs.forEach {
            adapter.add(ScanAdapter(it))

        }
    }

    private fun networkScanIP(CIDRAddress: String): Array<String> {

        //ping scan test

        val utils = SubnetUtils(CIDRAddress)
        val allIps: Array<String> = utils.info.allAddresses
    //appIps will contain all the ip address in the subnet
        return allIps

    }
    override fun onBackPressed() {

        viewModel.cancelScan()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // set up right help icon on toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pi_buddy_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_menu_help -> {
                val dialog =
                    HelpDialog()
                dialog.show(supportFragmentManager, "Help")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.addressCount.removeObservers(this)
        viewModel.ips.removeObservers(this)
        viewModel.cancelScan()
    }

}