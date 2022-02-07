package com.dbtechprojects.pibuddy.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class Connection(
    val ipAddress: String,
    val username: String,
    val password: String,
    val port : Int
): Parcelable

fun MutableList<Connection>.findByIp(ipAddress: String): Connection? {
    this.forEach {
        if (it.ipAddress == ipAddress) return it
    }
    return null
}