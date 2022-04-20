package com.dbtechprojects.pibuddy.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.utilities.SharedPref
import org.json.JSONObject

class CustomCommand (val IP: String): DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragement_container = container?.findViewById<FrameLayout>(R.id.fragement_container)
        val rootview = layoutInflater.inflate(R.layout.activity_custom_command, fragement_container, false)


        val button = rootview.findViewById<Button>(R.id.SaveCommandButton)
        val dialogtext = rootview.findViewById<EditText>(R.id.Dialog_CommandText)


        button.setOnClickListener {
            val command = dialogtext.text
            //Log.d(Result_Activity.TAG, command.toString())
            //Log.d(Result_Activity.TAG, IP)

            val pref = SharedPref.getSharedPref(activity?.applicationContext!!)

            val editor = pref?.edit()

            val strJson = pref?.getString(IP, null)


            val jresponse = JSONObject(strJson!!)

            // remove command if empty texg box is saved

            if (command.isEmpty()){
                jresponse.remove("CustomCommand")
                editor?.putString(IP, jresponse.toString())
                editor?.apply()
                Toast.makeText(activity, "Custom command removed",
                    Toast.LENGTH_LONG).show()
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                return@setOnClickListener
            }



            jresponse.put("CustomCommand", command.toString())

            if (command.toString().isNotEmpty()) {
                if (editor != null) {
                    editor.putString(IP, jresponse.toString())
                }

            }


            editor?.apply()

            Toast.makeText(activity, "Saving Custom Command, This will run on next connection",
                Toast.LENGTH_LONG).show()
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

            // run command

        }

        return rootview
    }
}