package com.example.pibuddy.Dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pibuddy.R
import com.example.pibuddy.Result_Activity
import kotlinx.android.synthetic.main.activity_custom_command.*
import kotlinx.android.synthetic.main.activity_custom_command.view.*
import kotlinx.android.synthetic.main.activity_main.*

class CustomCommand: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview = layoutInflater.inflate(R.layout.activity_custom_command, fragement_container, false)

        rootview.setOnClickListener {
            val command = Dialog_CommandText.text
            Log.d(Result_Activity.TAG, command.toString())
        }

        return rootview
    }
}