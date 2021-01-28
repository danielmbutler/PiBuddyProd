package com.dbtechprojects.pibuddy.activites

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dbtechprojects.pibuddy.Dialogs.CustomCommand
import com.dbtechprojects.pibuddy.Dialogs.HelpDialog
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.utilities.SharedPref
import kotlinx.android.synthetic.main.activity_result.*
import org.json.JSONObject


class Result_Activity: AppCompatActivity() {

    companion object{
        val TAG = "Result_Activity"
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val pref = SharedPref(this).sharedPreferences
        val editor = pref.edit()



        findViewById<View>(R.id.Main_Activity_text_dot_loader).visibility =
            View.VISIBLE

        val customCommandOutput = intent.getStringExtra("StoredCommandOutput")
        val results             = intent.getStringExtra("results")
        val diskspace           = intent.getStringExtra("diskspace")
        val cpuusage            = intent.getStringExtra("cpuusage")
        val memusage            = intent.getStringExtra("memusage")
        val IPAddress           = intent.getStringExtra("ipaddress")
        val Username            = intent.getStringExtra("username")
        val Password            = intent.getStringExtra("password")
        val StoredCommand       = intent.getStringExtra("StoredCommand")
        //Log.d("KEYS", "$IPAddress, $Username, $Password")

        if (IPAddress != null) {
            setupActionBar(IPAddress)
        }

        LoggedIn_Result_View.text            = results
        DiskSpace_Result_View.text           = (diskspace?.replace("[^0-9a-zA-Z:,]+".toRegex(), "") + "%" + " used") //replace all special charaters due to phantom space
        CPU_Result_View.text                 = cpuusage?.replace("[^.,a-zA-Z0-9]+".toRegex(), "") + "%" //replace all special charaters due to phantom space but keep '.'
        Mem_Result_View.text                 = memusage
        CustomCommand_Result_View.text       = customCommandOutput
        DiskSpace_Result_View.movementMethod = ScrollingMovementMethod()

        if(customCommandOutput != null){
            //Log.d(TAG, customCommandOutput)
            CustomCommandTextTitle.visibility = VISIBLE
            CustomCommand_Result_View.visibility = VISIBLE
        }

        //null titles so you cant edit
//        editTextTextPersonName3.keyListener = null
//        editTextTextPersonName2.keyListener = null
//        editTextTextPersonName.keyListener = null
//        ResultsTitle.keyListener = null


        findViewById<View>(R.id.Main_Activity_text_dot_loader).visibility =
            View.GONE

        // store successfull connection in shared pref




        if(StoredCommand != null){
            val Pidata = JSONObject("""{"Username":"${Username}", "Password":"$Password", "CustomCommand":"$StoredCommand"}""")
            editor.putString(IPAddress, Pidata.toString())
            editor.apply()
        } else{
            val Pidata = JSONObject("""{"Username":"${Username}", "Password":"$Password"}""")
            editor.putString(IPAddress, Pidata.toString())
            editor.apply()
        }



        AddCustomCommandButton.setOnClickListener {

                val dialog =
                    CustomCommand(IPAddress!!,this@Result_Activity)
                dialog.show(supportFragmentManager, "CustomCommand")

        }
    }
    private fun setupActionBar(IP: String) {

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24_white)
            actionBar.title = IP
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onBackPressed() {

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