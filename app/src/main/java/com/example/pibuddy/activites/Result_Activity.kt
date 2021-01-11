package com.example.pibuddy.activites

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.pibuddy.Dialogs.CustomCommand
import com.example.pibuddy.R
import kotlinx.android.synthetic.main.result.*
import org.json.JSONObject


class Result_Activity: AppCompatActivity() {

    companion object{
        val TAG = "Result_Activity"
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result)

        findViewById<View>(R.id.Scan_View_text_dot_loader).visibility =
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
        Log.d("KEYS", "$IPAddress, $Username, $Password")

        LoggedInUsersTextView.text       = results
        DiskSpaceTextView.text           = (diskspace?.replace("[^0-9a-zA-Z:,]+".toRegex(), "") + "%" + " used") //replace all special charaters due to phantom space
        CPUusageTextView.text            = cpuusage
        MemUsageTextView.text            = memusage
        CustomCommandTextView.text       = customCommandOutput
        DiskSpaceTextView.movementMethod = ScrollingMovementMethod()

        if(customCommandOutput != null){
            Log.d(TAG, customCommandOutput)
            CustomCommandTitle.visibility = VISIBLE
            CustomCommandTextView.visibility = VISIBLE
        }

        //null titles so you cant edit
        editTextTextPersonName3.keyListener = null
        editTextTextPersonName2.keyListener = null
        editTextTextPersonName.keyListener = null
        ResultsTitle.keyListener = null


        findViewById<View>(R.id.Scan_View_text_dot_loader).visibility =
            View.GONE

        // store successfull connection in shared pref

        val pref = applicationContext.getSharedPreferences(
            "Connection",
            0
        ) // 0 - for private mode
        val editor = pref.edit()


        if(StoredCommand != null){
            val Pidata = JSONObject("""{"Username":"${Username}", "Password":"$Password", "CustomCommand":"$StoredCommand"}""")
            editor.putString(IPAddress, Pidata.toString())
            editor.apply()
        } else{
            val Pidata = JSONObject("""{"Username":"${Username}", "Password":"$Password"}""")
            editor.putString(IPAddress, Pidata.toString())
            editor.apply()
        }



        val keys: Map<String, *> = pref.all
        Log.d("KEYS", keys.toString())
        Log.d("KEYS", intent.toString())




        BackButton.setOnClickListener {
            finish()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


        }
        Result_View_Add_Command.setOnClickListener {
            var dialog =
                CustomCommand(IPAddress!!)

            dialog.show(supportFragmentManager, "CustomCommand")


        }
    }
}