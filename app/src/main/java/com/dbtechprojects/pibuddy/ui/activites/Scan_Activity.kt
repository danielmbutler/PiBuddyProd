package com.dbtechprojects.pibuddy.ui.activites

import PiAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.dbtechprojects.pibuddy.Dialogs.HelpDialog
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.utilities.isPortOpen
import com.dbtechprojects.pibuddy.utilities.validate
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_scan_.*
import kotlinx.coroutines.*
import org.apache.commons.net.util.SubnetUtils
import java.lang.NullPointerException


class Scan_Activity : AppCompatActivity() {
    companion object{
        val TAG = "Scan_Activity"
    }

    private var items: List<String> = ArrayList()

    private lateinit var PiAdapter: PiAdapter

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private suspend fun NetworkScanIP(CIDRAddress: String): Array<String> {

        //ping scan test

        val utils = SubnetUtils(CIDRAddress)
        val allIps: Array<String> = utils.info.allAddresses
//appIps will contain all the ip address in the subnet
        return allIps

    }
    private var cancelled = "running"

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scan_)


        Scanning_Text_View.visibility = VISIBLE

        Scan_View_RecyclerView.adapter = adapter

        // faint line underneath each row
        Scan_View_RecyclerView.addItemDecoration(DividerItemDecoration(this@Scan_Activity, DividerItemDecoration.VERTICAL))


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


        // check for Wifi Address

        //get IP and subnet mask (comes out in CIDR *.*.*.*/*)

        // verify network connectivity

        var foundAddress = "none"

        try{
            val connectivityManager =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val addresses = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)!!.linkAddresses
            addresses.forEach {
                //println(it.address)
                //println(it)
                //println(it.prefixLength)
                if(validate(it.address.toString().replace("/",""))){
                    //Log.d("wifi", "${it.toString()} validated")
                    foundAddress = it.toString()
                }
            }
        } catch (ce: NullPointerException){

            Toast.makeText(this@Scan_Activity, "Wifi Connection Not Found, Please check Wifi", Toast.LENGTH_LONG).show()
            Scanning_Text_View.setText(resources.getString(R.string.returnwifiText))



        }





        if(foundAddress == "none"){
            Toast.makeText(this@Scan_Activity, "no Wifi found please check Wifi", Toast.LENGTH_LONG).show()
        } else { GlobalScope.launch(Dispatchers.IO) {
            val netAddresses = async { NetworkScanIP(foundAddress) }
            var addresscount = netAddresses.await().count()

            withContext(Dispatchers.Main){



                //set item click listener for recyclerview

                adapter.setOnItemClickListener{ item: Item<GroupieViewHolder>, view: View ->

                    val IP = item as PiAdapter
                    //Log.d(TAG, IP.IP)

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
                //Log.d(TAG, cancelled)

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

                    }
                } else{
                    //Log.d("pingtest", it.toString() + " " + pingtest.await())
                    addresscount--
                    //Log.d("IPCount", (addresscount).toString())

                    if(pingtest.await() == "connection successfull" ){
                        //Log.d(TAG, "$it + is available")
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

                        }


                    }
                }



            }
        }}


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

    // set up right help icon on toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pi_buddy_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
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