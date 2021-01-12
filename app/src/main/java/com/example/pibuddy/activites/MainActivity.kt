package com.example.pibuddy.activites


import android.content.Intent
import android.content.SharedPreferences.Editor
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.pibuddy.R
import com.example.pibuddy.utilities.executeRemoteCommand
import com.example.pibuddy.utilities.isPortOpen
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONException
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
            finish()

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

           menu.add(0,0,0, key).setOnMenuItemClickListener {

               Log.d("onclick listner", key)
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

           }.icon = ContextCompat.getDrawable(this,
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

                            Toast.makeText(this@MainActivity,"Connection Failure Please Retry..", Toast.LENGTH_SHORT).show()
                        }

                    } else {

                        // declare intent for result activity

                        var intent = Intent(this@MainActivity, Result_Activity::class.java)

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

                        // check for stored command for that IP
                        try{
                            val strJson = pref.getString(IPAddressText.text.toString(), null)

                            if(strJson != null){
                                val jresponse = JSONObject(strJson!!)
                                val storedCommand = jresponse.getString("CustomCommand")

                                Log.d("storedCommand", storedCommand!!)
                                if( storedCommand != null){
                                    val CustomCommandRun = async {
                                        executeRemoteCommand(
                                            UsernameText.text,
                                            PasswordText.text,
                                            IPAddressText.text,
                                            storedCommand
                                        )

                                    }
                                    withContext(Dispatchers.Main) {


                                        intent.putExtra("StoredCommandOutput", CustomCommandRun.await())
                                        intent.putExtra("StoredCommand", storedCommand)
                                    }
                            }

                            }

                        } catch (ce: JSONException){
                            Log.d("MainAcvitiy", "No Stored command found")
                        }



                        withContext(Dispatchers.Main) {


                            intent.putExtra("results",LoggedInUsers.await()
                            )
                            intent.putExtra("diskspace", DiskSpace.await())
                            intent.putExtra("memusage",MemUsage.await())
                            intent.putExtra("cpuusage",CpuUsage.await())

                            intent.putExtra("username", UsernameText.text.toString())
                            intent.putExtra("password",PasswordText.text.toString())
                            intent.putExtra("ipaddress",IPAddressText.text.toString())


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

                            startActivity(intent)
                            finish()
                        }

                    }


                }
            } else {
                Toast.makeText(this@MainActivity, "$validationtest...Please try again", Toast.LENGTH_SHORT).show()
            }


        }
    }





}








