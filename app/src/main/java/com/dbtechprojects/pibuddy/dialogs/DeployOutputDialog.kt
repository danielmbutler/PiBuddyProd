package com.dbtechprojects.pibuddy.dialogs

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.dbtechprojects.pibuddy.R

class DeployOutputDialog(private val output: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentContainer = container?.findViewById<FrameLayout>(R.id.fragement_container)
        val rootview = layoutInflater.inflate(R.layout.activity_help, fragmentContainer, false)


        val button = rootview.findViewById<Button>(R.id.Help_Button)
        val helptextView = rootview.findViewById<TextView>(R.id.Help_TextView)
        helptextView.movementMethod = ScrollingMovementMethod();
        helptextView.movementMethod = LinkMovementMethod.getInstance()

        helptextView.text = output


        button.setOnClickListener {

            //close helpDialog
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

        }

        return rootview
    }
}