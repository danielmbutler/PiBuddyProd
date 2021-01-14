package com.example.pibuddy.activites


import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.pibuddy.Dialogs.HelpDialog
import com.example.pibuddy.R
import com.example.pibuddy.utilities.executeRemoteCommand
import com.example.pibuddy.utilities.isPortOpen
import com.example.pibuddy.utilities.validate
import com.google.android.gms.ads.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.NullPointerException
import java.math.BigInteger
import java.net.Inet4Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder


class MainActivity : AppCompatActivity() {

    fun nullcheck(): String {
        if (IPAddressText.text.isEmpty()) {

            return "Missing IP"
        }
        if (UsernameText.text.isEmpty() ){

            return "Missing Username"
        }
        if (PasswordText.text.isEmpty()){

            return "Missing Password"
        }

        return "success"

    }

    lateinit var clearbutton: Button

    //AD setup

    private lateinit var mInterstitialAd: InterstitialAd


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("foundip", "dummylog")

       if(intent.getStringExtra("IPAddress") != null ) {
          val IP =  intent.getStringExtra("IPAddress")

           IPAddressText.setText(IP)

       }

        //intialise and build AD

        MobileAds.initialize(this@MainActivity)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712" //test unit

        mInterstitialAd.loadAd(AdRequest.Builder().build())

        // verify network connectivity

        try{
            val connectivityManager =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val addresses = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)!!.linkAddresses
        } catch (ce: NullPointerException){

            Toast.makeText(this@MainActivity, "Wifi Connection Not Found, Please check Wifi", Toast.LENGTH_LONG).show()


        }







        ScanButton.setOnClickListener {
            val intent = Intent(this, Scan_Activity::class.java)
            startActivity(intent)


        }

        //// slider

        var drawer: DrawerLayout? = null

            var toolbarid = toolbar.id

            setSupportActionBar(toolbar)
            drawer = findViewById(R.id.drawer_layout)
            val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawer!!.addDrawerListener(toggle)
            toggle.syncState()

        val mNavigationView = findViewById<View>(R.id.nav_viewer) as NavigationView
        mNavigationView.bringToFront()

        val pref = applicationContext.getSharedPreferences(
            "Connection",
            0
        ) // 0 - for private mode








       //get all preferences

              val keys: Map<String, *> = pref.all
            Log.d("KEYS", keys.toString())


        val menu = mNavigationView.menu

       for ((key, value) in keys) {

           Log.d("map values", key + ": " + value.toString())

           menu.add(0, 0, 0, key).setOnMenuItemClickListener {

               Log.d("onclick listner", key)
               pref.getString(this.title.toString(), null)
               val strJson = pref.getString(key, null)

               val jresponse = JSONObject(strJson)
               val UsernameFromJson = jresponse.getString("Username")
               val PasswordFromJson = jresponse.getString("Password")

               if (strJson != null) {
                   Log.d("onclick listner", strJson)
                   Log.d(
                       "onclick listner",
                       "Username: ${UsernameFromJson}, Password: ${PasswordFromJson} "
                   )
                   IPAddressText.setText(key)
                   UsernameText.setText(UsernameFromJson)
                   PasswordText.setText(PasswordFromJson)
               }



               drawer.closeDrawer(GravityCompat.START);
               true

           }.icon = ContextCompat.getDrawable(
               this,
               R.drawable.ic_computer
           )


           }
        //set click listner for draw button

        val headerview = mNavigationView.getHeaderView(0)
        val button =  headerview.findViewById<Button>(R.id.Nav_Header_Clear_Connection_Button)
        button.setOnClickListener {

            // Build Confirmation alert

            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    for (i in 0 until menu.size()) {

                        menu.removeGroup(0)
                    }
                    val editor: Editor = pref.edit()
                    editor.clear()
                    editor.apply()
                    drawer.closeDrawers()

                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }





            ConnectButton.setOnClickListener {

            val validationtest = nullcheck()
                Main_Activity_text_dot_loader.visibility = VISIBLE

            Log.d("Nullcheck", validationtest + IPAddressText.text)

            if (validationtest == "success"){

                GlobalScope.launch(Dispatchers.IO) {

                    //pingtest
                    val pingtest = async{
                        isPortOpen(
                            IPAddressText.text.toString(),
                            22,
                            3000
                        )
                    }
                    Log.d("pingtest", pingtest.await())

                    if (pingtest.await() == "false"){
                        withContext(Dispatchers.Main) {

                            Toast.makeText(
                                this@MainActivity,
                                "Connection Failure Please Retry..",
                                Toast.LENGTH_SHORT
                            ).show()
                            Main_Activity_text_dot_loader.visibility = INVISIBLE
                        }



                    } else {

                        // declare intent for result activity

                        var intent = Intent(this@MainActivity, Result_Activity::class.java)

                        // test command echo hello if fail show toast

                        val testcommand = async {  executeRemoteCommand(
                            UsernameText.text,
                            PasswordText.text,
                            IPAddressText.text, "echo hello"
                        ) }

                        Log.d("testcommand", testcommand.await())

                        if(!testcommand.await().contains("hello")){
                            withContext(Dispatchers.Main){

                                Toast.makeText(
                                    this@MainActivity,
                                    "Device Session failure, Please confirm username and password",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Main_Activity_text_dot_loader.visibility = INVISIBLE

                            }

                        } else {

                            val LoggedInUsers = async {
                            executeRemoteCommand(
                                UsernameText.text,
                                PasswordText.text,
                                IPAddressText.text, "who | cut -d' ' -f1 | sort | uniq\n"
                            )
                        }

                            val DiskSpace = async {
                                executeRemoteCommand(
                                    UsernameText.text,
                                    PasswordText.text,
                                    IPAddressText.text,
                                    "df -hl | grep \'root\' | awk \'BEGIN{print \"\"} {percent+=$5;} END{print percent}\' | column -t"
                                )
                            }
                            //
                            val MemUsage = async {
                                executeRemoteCommand(
                                    UsernameText.text,
                                    PasswordText.text,
                                    IPAddressText.text,
                                    "awk '/^Mem/ {printf(\"%u%%\", 100*\$3/\$2);}' <(free -m)"
                                )
                            }
                            val CpuUsage = async {
                                executeRemoteCommand(
                                    UsernameText.text,
                                    PasswordText.text,
                                    IPAddressText.text,
                                    "cat <(grep 'cpu ' /proc/stat) <(sleep 1 && grep 'cpu ' /proc/stat) | awk -v RS=\"\" '{print (\$13-\$2+\$15-\$4)*100/(\$13-\$2+\$15-\$4+\$16-\$5)}'"

                                )
                            }

                            // check for stored command for that IP
                            try{
                                val strJson = pref.getString(IPAddressText.text.toString(), null)

                                if(strJson != null){
                                    val jresponse = JSONObject(strJson!!)
                                    val storedCommand = jresponse.getString("CustomCommand")

                                    Log.d("storedCommand", storedCommand!!)
                                    if( storedCommand != null){
                                        // setup waiting dialogue

                                        withContext(Dispatchers.Main){
                                            Main_Custom_Command_Message.visibility = VISIBLE
                                        }
                                        val CustomCommandRun = async {
                                            executeRemoteCommand(
                                                UsernameText.text,
                                                PasswordText.text,
                                                IPAddressText.text,
                                                storedCommand
                                            )

                                        }
                                        withContext(Dispatchers.Main) {

                                            intent.putExtra(
                                                "StoredCommandOutput",
                                                CustomCommandRun.await()
                                            )
                                            intent.putExtra("StoredCommand", storedCommand)
                                        }
                                    }

                                }

                            } catch (ce: JSONException){
                                Log.d("MainAcvitiy", "No Stored command found")
                            }



                            withContext(Dispatchers.Main) {


                                intent.putExtra(
                                    "results", LoggedInUsers.await()
                                )
                                intent.putExtra("diskspace", DiskSpace.await())
                                intent.putExtra("memusage", MemUsage.await())
                                intent.putExtra("cpuusage", CpuUsage.await())

                                intent.putExtra("username", UsernameText.text.toString())
                                intent.putExtra("password", PasswordText.text.toString())
                                intent.putExtra("ipaddress", IPAddressText.text.toString())


                                val bundle = intent.extras
                                if (bundle != null) {
                                    for (key in bundle.keySet()) {
                                        Log.d(
                                            Result_Activity.TAG,
                                            key + " : " + if (bundle[key] != null) bundle[key] else "NULL"
                                        )
                                    }
                                }

                                Log.d("KEYS", intent.toString())

                                Main_Activity_text_dot_loader.visibility = INVISIBLE
                                Main_Custom_Command_Message.visibility = INVISIBLE
                                if (mInterstitialAd.isLoaded) {
                                    mInterstitialAd.show()

                                    mInterstitialAd.adListener = object : AdListener() {
                                        override fun onAdClosed() {

                                            startActivity(intent)
                                            finish()
                                        }

                                        override fun onAdFailedToLoad(p0: LoadAdError?) {
                                            super.onAdFailedToLoad(p0)
                                            startActivity(intent)
                                            finish()

                                        }

                                        override fun onAdClicked() {
                                            super.onAdClicked()
                                            startActivity(intent)
                                            finish()
                                        }

                                    }

                                } else {
                                    Log.d("TAG", "The interstitial wasn't loaded yet.")
                                }
                                Log.d("TAG", mInterstitialAd.responseInfo.toString())

                            }

                        }
                    }




                }
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "$validationtest...Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
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








