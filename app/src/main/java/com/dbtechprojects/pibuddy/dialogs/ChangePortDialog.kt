package com.dbtechprojects.pibuddy.dialogs

import android.os.Bundle
import android.util.Log
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

class ChangePortDialog (): DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragement_container = container?.findViewById<FrameLayout>(R.id.fragement_container)
        val rootview = layoutInflater.inflate(R.layout.dialog_port, fragement_container, false)


        val button = rootview.findViewById<Button>(R.id.dialog_port_editBtn)
        val dialogtext = rootview.findViewById<EditText>(R.id.dialog_port_editText)
        val pref = SharedPref.getSharedPref(activity?.applicationContext!!)

        pref.getInt("port", 22).let {
            dialogtext.setHint(it.toString())
        }




        button.setOnClickListener {
            val port = dialogtext.text

            if (port.isEmpty()){
                Toast.makeText(activity, "no port provided",
                    Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            val editor = pref?.edit()
            Log.d("port", "port: $port ")

            editor.putInt("port",port.toString().toInt())

            editor.commit()
            Toast.makeText(activity, "default port is now $port",
                Toast.LENGTH_LONG).show()

            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

        }

        return rootview
    }
}