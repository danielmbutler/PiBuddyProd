package com.dbtechprojects.pibuddy.ui.activites

import PiAdapter
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
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.dbtechprojects.pibuddy.Dialogs.HelpDialog
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.databinding.ActivityScanBinding
import com.dbtechprojects.pibuddy.ui.viewmodels.ScanViewModel
import com.dbtechprojects.pibuddy.utilities.NetworkUtils
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
    private val scanActivityBinding by lazy {
        ActivityScanBinding.inflate(layoutInflater)
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(scanActivityBinding.root)
        setupRV()
        setupClicks()


        // check for Wifi Address
        //get IP and subnet mask (comes out in CIDR *.*.*.*/*)
        // verify network connectivity
        getClientWifiAddress()



    }

    private fun getClientWifiAddress() {
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
            scanActivityBinding.ScanningTextView.text = resources.getString(R.string.returnwifiText)
        }

        if(clientAddress == "none"){
            Toast.makeText(this@Scan_Activity, "no Wifi found please check Wifi", Toast.LENGTH_LONG).show()
        } else {
            initScanIpObserver()
            val netAddresses = networkScanIP(clientAddress)
            // scan IPS and confirm activity
            viewModel.scanIPs(netAddresses)

            
        }
    }

    private fun initScanIpObserver() {
        viewModel.ips.observe(this, Observer { ip ->
            //add IP to IP list
            IPs.add(ip)
            refreshRecyclerViewMessages()
        })

        viewModel.addressCount.observe(this, Observer { count ->
            // update text view
            Log.d(TAG, "addresscount : $count ")
            val addtext = "Scanning for Devices with port 22 open ...... $count addresses remaining"

            // if scan is not cancelled set text
            if (!cancelled){
                scanActivityBinding.ScanningTextView.text = addtext
            }

        })
    }

    private fun setupClicks() {

        scanActivityBinding.ScanStopButton.setOnClickListener {
            cancelled = true
            viewModel.cancelScan()
            val messagetext = "Scan Stopped"
            scanActivityBinding.ScanningTextView.text = messagetext

            scanActivityBinding.ScanStopButton.text = getString(R.string.RestartScan)
            scanActivityBinding.ScanStopButton.setOnClickListener {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }

        scanActivityBinding.ScanBackButton.setOnClickListener {
            viewModel.cancelScan()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun setupRV(){

        adapter.setOnItemClickListener{ item: Item<GroupieViewHolder>, view: View ->

            val IP = item as PiAdapter
            //Log.d(TAG, IP.IP)

            intent= Intent(this@Scan_Activity,
                MainActivity::class.java)

            intent.putExtra("IPAddress", IP.IP)

            startActivity(intent)

            viewModel.cancelScan()
            finish()


        }
        scanActivityBinding.ScanViewRecyclerView.adapter = adapter

        // faint line underneath each row
        scanActivityBinding.ScanViewRecyclerView.addItemDecoration(DividerItemDecoration(this@Scan_Activity, DividerItemDecoration.VERTICAL))
    }

    private fun refreshRecyclerViewMessages(){
        println("RecyclerviewRefresh called + $IPs")
        adapter.clear()
        IPs.forEach {
            adapter.add(PiAdapter(it))

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


}