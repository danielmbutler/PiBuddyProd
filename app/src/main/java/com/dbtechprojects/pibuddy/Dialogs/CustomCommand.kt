package com.dbtechprojects.pibuddy.Dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.utilities.SharedPref
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

            val pref = SharedPref(context!!).sharedPreferences

            val editor = pref?.edit()

            val strJson = pref?.getString(IP, null)

            val jresponse = JSONObject(strJson!!)

            if (command.isEmpty()){
                jresponse.remove("CustomCommand")
                editor!!.putString(IP, jresponse.toString())
                editor!!.apply()
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }



            jresponse.put("CustomCommand", command.toString())

            val emptycheck = ""
            if (command.toString() !== emptycheck) {
                if (editor != null) {
                    editor.putString(IP, jresponse.toString())
                }

            }


            editor!!.apply()

            Toast.makeText(activity, "Saving Custom Command, This will run on next connection",
                Toast.LENGTH_LONG).show()
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

            // run command

        }

        return rootview
    }
}