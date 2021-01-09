package com.example.pibuddy.Dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.pibuddy.R
import kotlinx.android.synthetic.main.activity_main.*

class CustomCommand: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview = layoutInflater.inflate(R.layout.activity_custom_command, fragement_container, false)

        return rootview
    }
}