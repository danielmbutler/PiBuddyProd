package com.example.pibuddy.activites

import PiAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.pibuddy.R
import com.example.pibuddy.utilities.isPortOpen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_scan_.*
import kotlinx.coroutines.*
import org.apache.commons.net.util.SubnetUtils
import java.math.BigInteger
import kotlin.Boolean as Boolean1


class Scan_Activity : AppCompatActivity() {
    companion object{
        val TAG = "Scan_Activity"
    }

    private var items: List<String> = ArrayList()

    private lateinit var PiAdapter: PiAdapter

    private suspend fun NetworkScanIP(): Array<String> {

        //get IP

        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = BigInteger.valueOf(wm.dhcpInfo.netmask.toLong()).toString()
        Log.d(TAG,ipAddress)

        //ping scan test

        val utils = SubnetUtils("192.168.1.0/24")
        val allIps: Array<String> = utils.info.allAddresses
//appIps will contain all the ip address in the subnet
        return allIps

    }
    private var cancelled = "running"

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scan_)


        Scan_Stop_Button.setOnClickListener {
            cancelled = "STOP"

            Scan_Stop_Button.text = getString(R.string.RestartScan)
            Scan_Stop_Button.setOnClickListener {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }

        ScanBackButton.setOnClickListener {
            cancelled = "STOP"
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        GlobalScope.launch(Dispatchers.IO) {
            val netAddresses = async { NetworkScanIP() }
            var addresscount = netAddresses.await().count()

            withContext(Dispatchers.Main){

            Scan_View_text_dot_loader.visibility = VISIBLE
            Scanning_Text_View.visibility = VISIBLE

                Scan_View_RecyclerView.adapter = adapter

                // faint line underneath each row
                Scan_View_RecyclerView.addItemDecoration(DividerItemDecoration(this@Scan_Activity, DividerItemDecoration.VERTICAL))

                //set item click listener for recyclerview

                adapter.setOnItemClickListener{ item: Item<GroupieViewHolder>, view: View ->

                    val IP = item as PiAdapter
                    Log.d(TAG, IP.IP)

                    intent= Intent(this@Scan_Activity,
                        MainActivity::class.java)

                    intent.putExtra("IPAddress", IP.IP)

                    startActivity(intent)

                    cancelled = "STOP"
                    finish()


                }


                refreshRecyclerViewMessages()

            }


            netAddresses.await().forEach {
                Log.d(TAG, cancelled)

                val pingtest = async {
                    isPortOpen(
                        it.toString(),
                        22,
                        1000
                    )


                }
                val messagetext = "Scan Stopped"

                if(cancelled == "STOP"){
                    pingtest.cancel()
                    withContext(Dispatchers.Main) {
                        Scanning_Text_View.text = messagetext
                        Scan_View_text_dot_loader.visibility = INVISIBLE
                    }
                } else{
                    Log.d("pingtest", it.toString() + " " + pingtest.await())
                    addresscount--
                    Log.d("IPCount", (addresscount).toString())

                    if(pingtest.await() == "false" ){
                        Log.d(TAG, "$it + is available")
                        withContext(Dispatchers.Main){



                            IPs.add(it)
                            refreshRecyclerViewMessages()
                        }
                    }

                    withContext(Dispatchers.Main){
                        if(cancelled != "STOP"){
                            val addtext = "Scanning for Devices with port 22 open ...... $addresscount addresses remaining"
                            Scanning_Text_View.text = addtext
                        } else {
                            Scanning_Text_View.text = messagetext
                            Scan_View_text_dot_loader.visibility = INVISIBLE
                        }


                    }
                }



            }
        }
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    val IPs: MutableList<String> = mutableListOf()

    private fun refreshRecyclerViewMessages(){
        println("RecyclerviewRefresh called + $IPs")
        adapter.clear()
        IPs.forEach {
            adapter.add(PiAdapter(it))

        }
    }
    override fun onBackPressed() {

        cancelled = "STOP"
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}