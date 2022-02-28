package com.dbtechprojects.pibuddy.dialogs

import android.content.SharedPreferences
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
import androidx.recyclerview.widget.RecyclerView
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.adapters.CustomCommandAdapter
import com.dbtechprojects.pibuddy.models.CustomButton
import com.dbtechprojects.pibuddy.models.CustomButtons
import com.dbtechprojects.pibuddy.models.toCustomButtonList
import com.dbtechprojects.pibuddy.models.toJsonString
import com.dbtechprojects.pibuddy.utilities.SharedPref

class CustomButtonDialog (private val listener: CustomButtonListener): DialogFragment(), CustomCommandAdapter.OnCustomCommandClick {

    private lateinit var commandNameEditText: EditText
    private lateinit var commandEditText: EditText
    private lateinit var pref:  SharedPreferences
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragement_container = container?.findViewById<FrameLayout>(R.id.fragement_container)
        val rootview = layoutInflater.inflate(R.layout.dialog_custom_button, fragement_container, false)


        val confirmButton = rootview.findViewById<Button>(R.id.dialog_btn_editBtn)
        commandEditText = rootview.findViewById<EditText>(R.id.dialog_button_customCommand)
        commandNameEditText = rootview.findViewById<EditText>(R.id.dialog_button_customCommandName)
        recyclerView = rootview.findViewById<RecyclerView>(R.id.dialog_custom_btn_rv)

        pref = SharedPref.getSharedPref(activity?.applicationContext!!)

        setupRv()

        confirmButton.setOnClickListener {
            val command = commandEditText.text.toString()
            val name = commandNameEditText.text.toString()

            if (name.isEmpty() || command.isEmpty()){
                Toast.makeText(activity, "please provide a command and name",
                    Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            // update existing buttons
            val currentButtons = pref.getString("buttons", null)
            if (currentButtons != null){
                val listSerialized = currentButtons.toCustomButtonList()
                listSerialized?.list?.add(CustomButton(name = name, command = command))
                val editor = pref?.edit()

                editor.putString("buttons", listSerialized?.toJsonString())
                editor.commit()

                listener.onCustomButtonAdded()
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                return@setOnClickListener
            }


            val editor = pref?.edit()
            val buttons = CustomButtons(list = mutableListOf(CustomButton(name = name, command = command))).toJsonString()
            editor.putString("buttons", buttons)
            editor.commit()

            listener.onCustomButtonAdded()
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

        }

        return rootview
    }

    private fun setupRv() {
        recyclerView.adapter = CustomCommandAdapter(this, CustomButton.COMMAND_LIST)
    }



    override fun onClick(item: CustomButton) {
        Log.d("item", "item : ${item.name}")
        commandNameEditText.setText(item.name)
        commandEditText.setText(item.command)
    }
}

interface CustomButtonListener {
    fun onCustomButtonAdded()
}