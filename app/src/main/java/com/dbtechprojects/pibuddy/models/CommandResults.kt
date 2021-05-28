package com.dbtechprojects.pibuddy.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommandResults(
    val results: String, //LoggedInUsers
    val diskSpace: String,
    val memUsage: String,
    val cpuUsage: String
): Parcelable