package com.example.pibuddy


import android.content.Intent
import android.content.SharedPreferences.Editor
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.pibuddy.Dialogs.CustomCommand
import com.example.pibuddy.utilities.executeRemoteCommand
import com.example.pibuddy.utilities.isPortOpen
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.result.*
import kotlinx.coroutines.*
import org.json.JSONObject


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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       if(intent.getStringExtra("IPAddress") != null ) {
          val IP =  intent.getStringExtra("IPAddress")

           IPAddressText.setText(IP)

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
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawer!!.addDrawerListener(toggle)
            toggle.syncState()

        val mNavigationView = findViewById<View>(R.id.nav_viewer) as NavigationView
        mNavigationView.bringToFront();

        val pref = applicationContext.getSharedPreferences(
            "Connection",
            0
        ) // 0 - for private mode








       //get all preferences

              val keys: Map<String, *> = pref.getAll()
              var ItemId = 0

        val menu = mNavigationView.menu

       for ((key, value) in keys) {
           ItemId ++
           Log.d("map values", key + ": " + value.toString())

           menu.add(0,ItemId,0, key).setOnMenuItemClickListener {

                   Log.d("onclick listner", key)
               Log.d("onclick listner", it.itemId.toString())

                   pref.getString(this.title.toString(), null)
                   val strJson = pref.getString(key, null)

                   val jresponse = JSONObject(strJson)
                   val UsernameFromJson = jresponse.getString("Username")
                   val PasswordFromJson = jresponse.getString("Password")

                   if (strJson != null) {
                       Log.d("onclick listner", strJson)
                       Log.d("onclick listner", "Username: ${UsernameFromJson}, Password: ${PasswordFromJson} ")
                       IPAddressText.setText(key)
                       UsernameText.setText(UsernameFromJson)
                       PasswordText.setText(PasswordFromJson)
                   }



                   drawer.closeDrawer(GravityCompat.START);
                   true

               }.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_computer))


           }
        //set click listner for draw button

        val headerview = mNavigationView.getHeaderView(0)
        val button =  headerview.findViewById<Button>(R.id.Nav_Header_Clear_Connection_Button)
        button.setOnClickListener {
            for (i in 0 until menu?.size() ) {
                menu?.removeItem(i)
                menu?.removeItem(0)
                menu.removeGroup(0)
            }
            val editor: Editor = pref.edit()
            editor.clear()
            editor.apply()
            drawer.closeDrawers()
        }





            ConnectButton.setOnClickListener {
            ConnectButton.text = "Connect"
            val validationtest = nullcheck()

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
                    Log.d("pingtest",pingtest.await())

                    if (pingtest.await() == "false"){
                        withContext(Dispatchers.Main) {
                            ConnectButton.text = "Connection failure"
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
                        //"awk '/^Mem/ {printf(\"%u%%\", 100*\$3/\$2);}' <(free -m)"
                        val MemUsage = async {
                            executeRemoteCommand(
                                UsernameText.text,
                                PasswordText.text,
                                IPAddressText.text,
                                "hostname"
                            )
                        }
                        val CpuUsage = async {
                            executeRemoteCommand(
                                UsernameText.text,
                                PasswordText.text,
                                IPAddressText.text,
                                "mpstat | grep -A 5 \"%idle\" | tail -n 1 | awk -F \" \" '{print 100 -  \$ 12}'a"
                            )
                        }


                        withContext(Dispatchers.Main) {

                            var intent = Intent(this@MainActivity,  Result_Activity::class.java)
                            intent.putExtra("results",LoggedInUsers.await()
                            )
                            intent.putExtra("diskspace", DiskSpace.await())
                            intent.putExtra("memusage",MemUsage.await())
                            intent.putExtra("cpuusage",CpuUsage.await())

                            intent.putExtra("username", UsernameText.text)
                            intent.putExtra("password",PasswordText.text)
                            intent.putExtra("ipaddress",IPAddressText.text)

                            startActivity(intent)
                        }

                    }


                }
            } else {
                ConnectButton.text = validationtest + "...Please try again"
            }


        }
    }





}








