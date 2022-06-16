package com.dbtechprojects.pibuddy.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.repository.Repository
import com.dbtechprojects.pibuddy.utilities.Resource
import com.dbtechprojects.pibuddy.utilities.SharedPref
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class PerformanceWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each widget that belongs to this
        // provider.
        appWidgetIds.forEach { appWidgetId ->
            val sharedPref = SharedPref.getSharedPref(context)
            val devices = mutableListOf<WidgetDevice>()

            val port = sharedPref.getInt("port", 22)

            val keys: MutableMap<String, *> = sharedPref.all

            // setup menu Items with IPs
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

            val device = devices.getChosenDevice()


            fun nullViews() {
                val views =   RemoteViews(
                    context.packageName,
                    R.layout.performance_widget
                ).apply {
                    this.setTextViewText(R.id.widget_cpu, "N/A")
                    this.setTextViewText(R.id.widget_mem, "N/A")
                    this.setTextViewText(R.id.widget_disk, "N/A")
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }

            if (device == null){
                nullViews()
            } else {
                GlobalScope.launch {
                    val results = Repository.runPiCommands(device.ipAddress, device.username, device.password, null, port, GlobalScope)
                    Log.d("device", "${results.data}")
                    when(results){
                        is Resource.Error -> {
                            withContext(Dispatchers.Main){
                                nullViews()
                            }

                        }
                        is Resource.Success -> {
                           val views =  RemoteViews(
                                context.packageName,
                                R.layout.performance_widget
                            ).apply {
                               this.setTextViewText(R.id.widget_cpu, "CPU\n${results.data?.cpuUsage}")
                               this.setTextViewText(R.id.widget_mem, "MEM\n${results.data?.memUsage}")
                               this.setTextViewText(R.id.widget_disk, "HDD\n${(results.data?.diskSpace?.replace(
                                   "[^0-9a-zA-Z:,]+".toRegex(),
                                   ""
                               ) + "%")}")
                            }

// Tell the AppWidgetManager to perform an update on the current
                            // widget.
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }
                    }
                }


            }



        }
    }


}


data class WidgetDevice(
    val ipAddress: String,
    val username: String,
    val password: String,
    val isChosen: Boolean
)

fun MutableList<WidgetDevice>.getChosenDevice() : WidgetDevice? {
    if(this.isEmpty()) return null
    this.forEach {
        if (it.isChosen) return it
    }
    return this[0]
}

fun MutableList<WidgetDevice>.order() : MutableList<WidgetDevice>{
    if (this.isEmpty()) return mutableListOf()
    val newlist = mutableListOf<WidgetDevice>()
    this.forEach {
        if (it.isChosen){
            newlist.add(0, it)
        } else{
            newlist.add(it)
        }
    }
    return newlist

}

fun MutableList<WidgetDevice>.getIps() : Array<String>{
    val items = mutableListOf<String>()
    this.forEach {
        items.add(it.ipAddress)
    }
    return items.toTypedArray()
}

