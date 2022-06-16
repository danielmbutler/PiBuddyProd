package com.dbtechprojects.pibuddy.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.utilities.SharedPref
import com.dbtechprojects.pibuddy.widget.WidgetDevice
import org.json.JSONObject
import android.widget.ArrayAdapter
import com.dbtechprojects.pibuddy.widget.getIps
import com.dbtechprojects.pibuddy.widget.order


class WidgetDialog (): DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragement_container = container?.findViewById<FrameLayout>(R.id.fragement_container)
        val rootview = layoutInflater.inflate(R.layout.dialog_widget, fragement_container, false)


        val button = rootview.findViewById<Button>(R.id.dialog_widget_editBtn)
        val spinner = rootview.findViewById<Spinner>(R.id.spinner)
        val spinnerImg = rootview.findViewById<ImageView>(R.id.spinner_img)
        val sharedPref = SharedPref.getSharedPref(activity?.applicationContext!!)

        spinnerImg.setOnClickListener {
            spinner.performClick()
        }

        val devices = mutableListOf<WidgetDevice>()

        val keys: MutableMap<String, *> = sharedPref.all
        for ((key, value) in keys) {
            if (key == "adcount") {
                sharedPref.edit().remove("adcount").apply()
            } else if (key != "port" && key != "buttons") {
                val strJson = sharedPref.getString(key, null)

                val jresponse = JSONObject(strJson)
                val UsernameFromJson = jresponse.getString("Username")
                val PasswordFromJson = jresponse.getString("Password")

                val isWidget = try {
                    jresponse.getBoolean("isWidget")
                } catch (e: Exception) {
                    false
                }

                devices.add(WidgetDevice(key, UsernameFromJson, PasswordFromJson, isWidget))
                Log.d(
                    "device",
                    "isWidget = $isWidget, $UsernameFromJson , $PasswordFromJson , $key "
                )
            }
        }

        if (devices.isNotEmpty()){
            // setup spinner
            val items = devices.order().getIps()

            val adapter: ArrayAdapter<String> =
                ArrayAdapter<String>(requireContext(), R.layout.spinner_item, items)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter
        }

         fun updateDevice() {
             val chosenDevice = spinner.selectedItem as String
             val strJson = sharedPref.getString(chosenDevice, null)
             if (strJson != null){
                 val jresponse = JSONObject(strJson)
                 jresponse.put("isWidget", true)
                 val editor = sharedPref.edit()
                 editor.putString(chosenDevice, jresponse.toString())
             }
             Toast.makeText(activity, "Device with ip $chosenDevice will now be used for the widget",
                 Toast.LENGTH_LONG).show()
             activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

        }


        button.setOnClickListener {
            if (devices.isEmpty()){
                Toast.makeText(activity, "Devices will be available when you make a successful connection.",
                    Toast.LENGTH_LONG).show()
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            } else {
                updateDevice()
            }





        }

        return rootview
    }


}