package com.example.pibuddy.Dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.pibuddy.R
import com.example.pibuddy.activites.Result_Activity
import kotlinx.android.synthetic.main.activity_custom_command.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class CustomCommand (val IP: String): DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview = layoutInflater.inflate(R.layout.activity_custom_command, fragement_container, false)


         val button = rootview.findViewById<Button>(R.id.SaveCommandButton)


        button.setOnClickListener {
            val command = Dialog_CommandText.text
            //Log.d(Result_Activity.TAG, command.toString())
            //Log.d(Result_Activity.TAG, IP)

            if (command.isEmpty()){
                return@setOnClickListener

            }

            val pref = context?.getSharedPreferences(
                "Connection",
                0
            ) // 0 - for private mode

            val editor = pref?.edit()

            val strJson = pref?.getString(IP, null)

            val jresponse = JSONObject(strJson!!)

            jresponse.put("CustomCommand", command.toString())

            val emptycheck = ""
            if (command.toString() !== emptycheck) {
                if (editor != null) {
                    editor.putString(IP, jresponse.toString())
                }

            }


            editor!!.apply()


            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

            // run command

        }

        return rootview
    }
}