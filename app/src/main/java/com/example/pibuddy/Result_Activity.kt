package com.example.pibuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.pibuddy.Dialogs.CustomCommand
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.result.*
import org.json.JSONObject

class Result_Activity: AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result)

        findViewById<View>(R.id.Scan_View_text_dot_loader).visibility =
            View.VISIBLE

        val results = intent.getStringExtra("results")
        val diskspace = intent.getStringExtra("diskspace")
        val cpuusage = intent.getStringExtra("cpuusage")
        val memusage = intent.getStringExtra("memusage")
        val IPAddress = intent.getStringExtra("ipaddress")
        val Username = intent.getStringExtra("username")
        val Password = intent.getStringExtra("password")

        LoggedInUsersTextView.text  = results
        DiskSpaceTextView.text      = (diskspace?.replace("[^0-9a-zA-Z:,]+".toRegex(), "") + "%" + " used") //replace all special charaters due to phantom space
        CPUusageTextView.text       = cpuusage
        MemUsageTextView.text       = memusage
        DiskSpaceTextView.setMovementMethod(ScrollingMovementMethod());

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


        val Pidata = JSONObject("""{"Username":"${Username}", "Password":"$Password"}""")
        editor.putString(IPAddress, Pidata.toString())



        editor.apply()




        BackButton.setOnClickListener {
            finish()
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)


        }
        Result_View_Add_Command.setOnClickListener {
            var dialog =
                CustomCommand()
            dialog.show(supportFragmentManager, "CustomCommand")
        }
    }
}